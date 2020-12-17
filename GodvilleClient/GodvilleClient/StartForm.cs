using Grpc.Net.Client;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using static GodvilleClient.GodvilleService;

namespace GodvilleClient
{
    public partial class StartForm : Form
    {
        public StartForm()
        {
            InitializeComponent();
            lblHeroName.Text = Program.Client.HeroName;
            lblYourHealth.Text = Program.Client.CountLives.ToString();
        }

        private void btnStartDuel_Click(object sender, EventArgs e)
        {
            try
            {
                Thread readerThread = new Thread(new ThreadStart(ReadClientMsg));
                readerThread.Start();
            }
            catch (Exception ex)
            {
                Logger.AddErrorMessage(ex.Message);
            }
        }

        void ToggleDuelState(bool duelStarted)
        {
            btnGood.SetPropertyThreadSafe(() => btnGood.Visible, duelStarted);
            btnBad.SetPropertyThreadSafe(() => btnBad.Visible, duelStarted);
            btnStartDuel.SetPropertyThreadSafe(() => btnStartDuel.Visible, !duelStarted);
            if (!duelStarted)
            {
                lblEnemyHealth.SetPropertyThreadSafe(() => lblEnemyHealth.Text, "");
                lblEnemyName.SetPropertyThreadSafe(() => lblEnemyName.Text, "");
                TestFormControlHelper.ControlInvoke(lvDuelHistory, () => lvDuelHistory.Items.Clear());
            }
        }

        void ReadClientMsg()
        {
            string serverIp;
            //try
            //{
            //    GrpcChannel channel = Connection.GetDispatcherChannel();
            //    var client = new GodvilleServiceClient(channel);
            //    serverIp = client.StartDuel(new ClientId { Id = Program.Client.Id }).Ip;
            //}
            //catch (Exception e)
            //{
            //    //Выбранный диспетчер вдруг умер после проверки на активность
            //    Logger.AddErrorMessage(e.Message);
            //    MessageBox.Show("Дуэль не может быть начата");
            //    return;
            //}

            //заглушка
            serverIp = "localhost:8006";

            var lines = serverIp.Split(":");
            //var ping = new Ping();
            //var reply = ping.Send(lines[0], 200); // 0,2 минуты тайм-аут
            //if (!reply.Status.ToString().Equals("Success"))
            //{
            //    MessageBox.Show("Дуэль закончилась, не начавшись: ваш противник внезано провалился сквозь землю");
            //    return;
            //}
            //else
            //    ToggleDuelState(true);


            int port = int.Parse(lines[1]);
            string server = lines[1];

            IFormatter formatter = new BinaryFormatter();

            using (TcpClient tcpClient = new TcpClient(lines[0], port))
            {
                //tcpClient.Connect(server, port);
                NetworkStream networkStream = tcpClient.GetStream();
                WriteStream.WriteNetworkStream = tcpClient.GetStream();
                BinaryReader sr = new BinaryReader(networkStream);
                BinaryWriter sw = new BinaryWriter(networkStream);
                sr.BaseStream.ReadTimeout = 1000; // таймаут на отклик сервера - 0,2 минуты
                sw.Write(Program.Client.Id); // послать серверу свой id и начать взаимодействие
                sw.Flush();
                while (true)
                {
                    Model.ClientMsg clientMsg;
                    try
                    {
                        MemoryStream ms = new MemoryStream();
                        networkStream.CopyTo(ms);
                        ms.ToArray();
                        clientMsg = (Model.ClientMsg) formatter.Deserialize(ms);
                    }
                    catch (IOException e)
                    {
                        MessageBox.Show("Вашего противника унесла хищная птица. Дуэль завершена, теперь вы можете найти ее в статистике");
                        Logger.AddErrorMessage(e.Message);
                        ToggleDuelState(false);
                        return;
                    }
                    if (clientMsg != null)
                    {
                        // заглушка
                        //clientMsg.Type = 4;
                        //clientMsg.EnemyName = "bugurt";
                        //clientMsg.EnemyLives = 89;
                        //clientMsg.IsEven = true;
                        //clientMsg.Phrase = "ФРАЗАФРАЗАФРАЗА";
                        //

                        if (clientMsg.Type == 4)
                        {
                            lblEnemyName.SetPropertyThreadSafe(() => lblEnemyName.Text, clientMsg.EnemyName);
                        }
                        if (clientMsg.Glas != -1)
                        {
                            string glasMsg = clientMsg.Glas == 0 ?
                                "Противник сделал плохо. Ваше здоровье уменьшилось" :
                                "Противник сделал хорошо. Его герой вылечился";
                            TestFormControlHelper.ControlInvoke(lvDuelHistory, () => lvDuelHistory.Items.Add(glasMsg));
                            TestFormControlHelper.ControlInvoke(
                                lvDuelHistory,
                                () => lvDuelHistory.Items[lvDuelHistory.Items.Count - 1].BackColor = Color.Cyan);
                        }
                        btnGood.SetPropertyThreadSafe(() => btnGood.Enabled, clientMsg.IsEven);
                        btnBad.SetPropertyThreadSafe(() => btnBad.Enabled, clientMsg.IsEven);

                        TestFormControlHelper.ControlInvoke(lvDuelHistory, () => lvDuelHistory.Items.Add(clientMsg.Phrase));
                        lblEnemyHealth.SetPropertyThreadSafe(() => lblEnemyHealth.Text, clientMsg.EnemyLives.ToString());
                        lblYourHealth.SetPropertyThreadSafe(() => lblYourHealth.Text, clientMsg.Lives.ToString());

                        if (clientMsg.Type == 0)
                        {
                            MessageBox.Show("Дуэль завершена, теперь вы можете найти ее в статистике");
                            ToggleDuelState(false);
                            return;
                        }
                    }
                }
            }
        }


        private void btnGood_Click(object sender, EventArgs e)
        {
            if (WriteStream.WriteNetworkStream == null)
                return;
            string response = "1";
            byte[] data = Encoding.UTF8.GetBytes(response);
            WriteStream.WriteNetworkStream.Write(data, 0, data.Length); // сказать серверу, что клиент сделал хорошо
        }

        private void btnBad_Click(object sender, EventArgs e)
        {
            if (WriteStream.WriteNetworkStream == null)
                return;
            string response = "0";
            byte[] data = Encoding.UTF8.GetBytes(response);
            WriteStream.WriteNetworkStream.Write(data, 0, data.Length); // сказать серверу, что клиент сделал плохо
        }

        private void btnGetStat_Click(object sender, EventArgs e)
        {
            Thread readerThread = new Thread(new ThreadStart(StatisticReader));
            readerThread.Start();
        }

        void StatisticReader()
        {
            try
            {
                GrpcChannel channel = Connection.GetDispatcherChannel();
                var client = new GodvilleServiceClient(channel);
                var statistic = client.GetStatistic(new ClientId { Id = Program.Client.Id });
                MessageBox.Show(string.Format("Всего побед: {0}, поражений: {1}", statistic.Wins, statistic.Loses));
            }
            catch (Exception e)
            {
                MessageBox.Show("Простите, статистика временно не может быть получена");
                Logger.AddErrorMessage(e.Message);
            }
        }

        private void linkLogout_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            try
            {
                var channel = Connection.GetDispatcherChannel();
                var client = new GodvilleServiceClient(channel);
                client.Logout(new ClientId { Id = Program.Client.Id });
            }
            catch (Exception ex)
            {
                Logger.AddErrorMessage(ex.Message);
                MessageBox.Show("Операция временно недоступна");
                return;
            }

            Close();
        }

        private void StartForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (WriteStream.WriteNetworkStream == null)
                return;
            WriteStream.WriteNetworkStream.Close();
        }
    }

    class WriteStream
    {
        public WriteStream()
        {        }
        public static NetworkStream WriteNetworkStream { get; set; }
    }
}

