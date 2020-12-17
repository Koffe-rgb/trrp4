using Grpc.Net.Client;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using static GodvilleClient.GodvilleService;

namespace GodvilleClient
{
    public class Connection
    {
        public static GrpcChannel GetDispatcherChannel()
        {
            int dispatcher = 0;
            while (!CheckDispatcherIsAlive(dispatcher) && dispatcher < Model.Config.DispatcherList.Count)
                dispatcher++;
            if (dispatcher < Model.Config.DispatcherList.Count)
                return GrpcChannel.ForAddress(Model.Config.DispatcherList[dispatcher]);
            return GrpcChannel.ForAddress(Model.Config.DispatcherList[0]);
        }

        public static bool CheckDispatcherIsAlive(int index)
        { 
            try
            {
                var channel = GrpcChannel.ForAddress(Model.Config.DispatcherList[index]);
                var client = new GodvilleServiceClient(channel);
                client.Check(new Empty { }, deadline: DateTime.UtcNow.AddSeconds(5));
                return true;
            }
            catch (Exception)
            { // диспетчер недоступен, ничего не делаем
                return false;
            }
        }

    }
}
