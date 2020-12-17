using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GodvilleClient.Model
{
    public class RegisterData : LoginData
    {
        public string Nickname { get; set; }
        public string HeroName { get; set; }
        public RegisterData () : base () { }
        public RegisterData(string login, string password, string nickname) : base (login, password) {
            Nickname = nickname;
        }
    }
}
