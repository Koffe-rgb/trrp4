using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Formatters.Binary;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace GodvilleClient.Model
{
    [Serializable]
    public class ClientData 
    {
        public long Id { get; set; }
        public string Nickname { get; set; }
        public long CountLives { get; set; }
        public string HeroName { get; set; }

        public ClientData()
        {
            Id = -1;
        }
        public void TryGetClient()
        {
            try
            {
                using (StreamReader sr = new StreamReader(Config.MyIdFilePath))
                {
                    string json = sr.ReadLine();
                    Deserialize(json);
                }
            }
            catch (Exception e)
            {
                Logger.AddErrorMessage(e.Message);
            }
            return;
        }

        public void Deserialize(string json)
        {
            ClientData deserialize = JsonSerializer.Deserialize<ClientData>(json);
            Id = deserialize.Id;
            Nickname = deserialize.Nickname;
            CountLives = deserialize.CountLives;
            HeroName = deserialize.HeroName;
        }
        public void SetClientData()
        {
            try
            {
                string json = JsonSerializer.Serialize(this);
                File.WriteAllText(Model.Config.MyIdFilePath, json);
            } catch(Exception e)
            {
                Logger.AddErrorMessage(e.Message);
            }
        }

        public long GetLivesCount()
        {
            return 100;
        }
    }
}
