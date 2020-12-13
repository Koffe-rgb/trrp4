using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GodvilleClient.Model
{
    class Config
    {
        public static string dateTimeNow = TimeZone.CurrentTimeZone.ToLocalTime(DateTime.Now).ToString().Replace(":", ".");
        public static string ErrorOutputFilePath { get; set; } = "log\\log " + dateTimeNow +".txt";
        public static string MyIdFilePath { get; set; } = "myid.txt";
        public static List<string> DispatcherList { get; set; } = new List<string>() { 
            "http://192.168.100.6:8024", 
            "http://192.168.100.6:8025",
            "http://192.168.100.6:8026"};
    }
}
