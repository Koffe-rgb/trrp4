using Grpc.Net.Client;
using Newtonsoft.Json;
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
        Thread readerThread;
        public StartForm()
        {
            InitializeComponent();
            lvDuelHistorySetVerticalScroll();
            lblHeroName.Text = Program.Client.HeroName;
            lblYourHealth.Text = Program.Client.CountLives.ToString();
        }

        void lvDuelHistorySetVerticalScroll()
        {
            lvDuelHistory.Scrollable = true;
            lvDuelHistory.View = View.Details;
            ColumnHeader header = new ColumnHeader();
            header.Text = "";
            header.Name = "col1";
            header.Width = lvDuelHistory.Width;
            lvDuelHistory.AutoResizeColumns(ColumnHeaderAutoResizeStyle.HeaderSize);
            lvDuelHistory.Columns.Add(header);

        }

        private void btnStartDuel_Click(object sender, EventArgs e)
        {
            try
            {
                readerThread = new Thread(new ThreadStart(ReadClientMsg));
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
            btnGood.SetPropertyThreadSafe(() => btnGood.Enabled, false);
            btnBad.SetPropertyThreadSafe(() => btnBad.Visible, duelStarted);
            btnBad.SetPropertyThreadSafe(() => btnBad.Enabled, false);

            btnStartDuel.SetPropertyThreadSafe(() => btnStartDuel.Visible, !duelStarted);
            btnGetStat.SetPropertyThreadSafe(() => btnGetStat.Enabled, !duelStarted);
            if (!duelStarted)
            {
                lblEnemyHealth.SetPropertyThreadSafe(() => lblEnemyHealth.Text, "");
                lblEnemyName.SetPropertyThreadSafe(() => lblEnemyName.Text, "");
                TestFormControlHelper.ControlInvoke(lvDuelHistory, () => lvDuelHistory.Items.Clear());
            }
            
        }

        void ToggleMyHod(bool isMyHod)
        {
            btnGood.SetPropertyThreadSafe(() => btnGood.Enabled, isMyHod);
            btnBad.SetPropertyThreadSafe(() => btnBad.Enabled, isMyHod);
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
            serverIp = "127.0.0.1:8017";

            var lines = serverIp.Split(":");
            int port = int.Parse(lines[1]);
            string server = lines[1];

            try
            {
                using (TcpClient tcpClient = new TcpClient(lines[0], port))
                {
                    NetworkStream networkStream = tcpClient.GetStream();
                    StreamReader sr = new StreamReader(networkStream);
                    StreamWriter sw = new StreamWriter(networkStream);
                    sr.BaseStream.ReadTimeout = 20 * 1000;
                    sw.WriteLine(Program.Client.Id.ToString());
                    sw.Flush();

                    string input;
                    ToggleDuelState(true);
                    while (true)
                    {
                        Model.ClientMsg clientMsg;
                        try
                        {
                            if ((input = sr.ReadLine()) != null)
                            {
                                clientMsg = JsonConvert.DeserializeObject<Model.ClientMsg>(input);
                            }
                            else
                                continue;
                        }
                        catch (IOException e)
                        {
                            MessageBox.Show("Дуэль была прервана обстоятельствами, теперь вы можете найти ее в статистике");
                            Logger.AddErrorMessage(e.Message);
                            ToggleDuelState(false);
                            return;
                        }
                        if (clientMsg != null)
                        {
                            bool isHodEven = clientMsg.HodNum % 2 == 0;

                            if (clientMsg.Type != 4)
                                ToggleMyHod(isHodEven);

                            if (clientMsg.Type == 4)
                                lblEnemyName.SetPropertyThreadSafe(() => lblEnemyName.Text, clientMsg.EnemyName);

                            TestFormControlHelper.ControlInvoke(lvDuelHistory, () => lvDuelHistory.Items.Add(clientMsg.Phrase));
                            if (clientMsg.Glas != -1 && clientMsg.Type != 4)
                            {
                                string glasMsg;
                                if (isHodEven) // в ход клиента приходит глас от противника
                                {
                                    glasMsg = clientMsg.Glas == 0 ?
                                        "Противник сделал плохо. Ваше здоровье уменьшилось" :
                                        "Противник сделал хорошо. Его герой вылечился";
                                }
                                else // в ход противника видим глас с нашего прошлого хода (если сработал)
                                {
                                    glasMsg = clientMsg.Glas == 0 ?
                                        "Вы сделали плохо. Здоровье противника уменьшилось" :
                                        "Вы сделали хорошо. Ваш герой подлатал раны";
                                }
                                TestFormControlHelper.ControlInvoke(lvDuelHistory, () => lvDuelHistory.Items.Add(glasMsg));
                                TestFormControlHelper.ControlInvoke(
                                    lvDuelHistory,
                                    () => lvDuelHistory.Items[lvDuelHistory.Items.Count - 1].BackColor = Color.PeachPuff);
                            }

                            TestFormControlHelper.ControlInvoke(lvDuelHistory, () => lvDuelHistory.EnsureVisible(lvDuelHistory.Items.Count - 1));
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
            catch (Exception e)
            {
                MessageBox.Show("Дуэль закончилась, не начавшись: ваш противник внезано провалился сквозь землю");
                Logger.AddErrorMessage(e.Message);
                return;
            }
        }


        private void btnGood_Click(object sender, EventArgs e)
        {
            btnGood.Enabled = false;
            btnBad.Enabled = false;
            if (WriteStream.WriteNetworkStream == null)
                return;
            string response = "1";
            byte[] data = Encoding.UTF8.GetBytes(response);
            WriteStream.WriteNetworkStream.Write(data, 0, data.Length); // сказать серверу, что клиент сделал хорошо   
        }

        private void btnBad_Click(object sender, EventArgs e)
        {
            btnGood.Enabled = false;
            btnBad.Enabled = false;
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
                Program.Client.ClearClientData();
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
            if (readerThread != null)
                readerThread.Interrupt();
        }

        private void StartForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            if (readerThread != null)
                readerThread.Interrupt();
        }
    }

    class WriteStream
    {
        public WriteStream()
        { }
        public static NetworkStream WriteNetworkStream { get; set; }
    }
}

