using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GodvilleClient
{
    class Logger
    {
        public static void AddErrorMessage(string message)
        {
            using (StreamWriter streamWriter = File.AppendText(Model.Config.ErrorOutputFilePath))
            {
                streamWriter.WriteLine(TimeZone.CurrentTimeZone.ToLocalTime(DateTime.Now).ToString() + "\t" + message);
            }
        }
    }
}
