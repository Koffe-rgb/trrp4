
using Grpc.Core;
using Grpc.Net.Client;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;
using System.Windows.Forms;
using static GodvilleClient.GodvilleService;

namespace GodvilleClient
{
    static class Program
    {
        public static Model.ClientData Client { get; set; } = new Model.ClientData();
        /// <summary>
        ///  The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.SetHighDpiMode(HighDpiMode.SystemAware);
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            Client.TryGetClient();
            Model.LoginData loginData = new Model.LoginData();
            while (Client.Id == -1)
            {
                LoginForm loginForm = new LoginForm(loginData);
                DialogResult result = loginForm.ShowDialog();
                // логин
                if (result == DialogResult.OK)
                {
                    UserLoginOuput userLoginOutput;
                    try
                    {
                        using var channel = Connection.GetDispatcherChannel();
                        var serviceClient = new GodvilleServiceClient(channel);
                        userLoginOutput = serviceClient.Login(
                            new LoginData
                            {
                                Login = loginData.Login,
                                Password = loginData.Password,
                            });
                    }
                    catch (Exception e)
                    {
                        Logger.AddErrorMessage(e.Message);
                        return;
                    }
                    
                    if (userLoginOutput.Id == -1)
                        MessageBox.Show("Ќеверное им€ пользовател€ или пароль");
                    else
                    {
                        Client.Id = userLoginOutput.Id;
                        Client.Nickname = userLoginOutput.Nickname;
                        Client.CountLives = userLoginOutput.HealthCount;
                        Client.HeroName = userLoginOutput.HeroName;
                        Client.SetClientData();
                    }
                }
                // регистраци€
                else if (result == DialogResult.Ignore)
                {
                    Model.RegisterData regData = new Model.RegisterData();
                    RegisterForm rf = new RegisterForm(regData);
                    if (rf.ShowDialog() == DialogResult.OK)
                    {
                        using var channel = Connection.GetDispatcherChannel();
                        var serviceClient = new GodvilleServiceClient(channel);

                        UserRegOutput userRegOutput;
                        try
                        {
                            userRegOutput = serviceClient.Register(
                                new RegisterData
                                {
                                    LoginData = new LoginData { Login = regData.Login, Password = regData.Password},
                                    Nickname = regData.Nickname,
                                    Heroname = regData.HeroName
                                });
                        } catch(Exception e)
                        {
                            Logger.AddErrorMessage(e.Message);
                            return;
                        }
                        
                        if (userRegOutput.Id == -1)
                            MessageBox.Show("Ћогин зан€т, выберите другой");
                        else
                        {
                            Client.Id = userRegOutput.Id;
                            Client.Nickname = userRegOutput.Nickname;
                            Client.CountLives = Client.GetLivesCount();
                            Client.HeroName = regData.HeroName;
                            Client.SetClientData();
                        }
                    }
                    else
                        return;
                }
                // завершение работы
                else if (result == DialogResult.Cancel)
                    return;
            }
            Application.Run(new StartForm());
        }
    }
}
