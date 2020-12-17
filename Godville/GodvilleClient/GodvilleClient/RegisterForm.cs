using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GodvilleClient
{
    public partial class RegisterForm : Form
    {
        Model.RegisterData regData;
        public RegisterForm(Model.RegisterData regData)
        {
            InitializeComponent();
            this.regData = regData;
            btnOk.Enabled = OpenOkBtn();
        }

        bool OpenOkBtn()
        {
            bool login = !string.IsNullOrEmpty(txtLogin.Text);
            bool password = !string.IsNullOrEmpty(txtPassword.Text);
            bool passwordCheck = !string.IsNullOrEmpty(txtPasswordCheck.Text);
            bool nick = !string.IsNullOrEmpty(txtNickname.Text);
            bool heroName = !string.IsNullOrEmpty(txtHeroName.Text);
            return login && password && passwordCheck && nick && heroName; 
        }

        private void btnOk_Click(object sender, EventArgs e)
        {
            if (!txtPassword.Text.Equals(txtPasswordCheck.Text))
            {
                MessageBox.Show("Пароли должны совпадать!");
                DialogResult = DialogResult.None;
            }
            else
            {
                regData.Login = txtLogin.Text;
                regData.Password = txtPassword.Text;
                regData.Nickname = txtNickname.Text;
                regData.HeroName = txtHeroName.Text;
            }
        }

        private void txtNickname_TextChanged(object sender, EventArgs e)
        {
            btnOk.Enabled = OpenOkBtn();
        }

        private void txtLogin_TextChanged(object sender, EventArgs e)
        {
            btnOk.Enabled = OpenOkBtn();
        }

        private void txtPassword_TextChanged(object sender, EventArgs e)
        {
            btnOk.Enabled = OpenOkBtn();
        }

        private void txtPasswordCheck_TextChanged(object sender, EventArgs e)
        {
            btnOk.Enabled = OpenOkBtn();
        }

        private void txtHeroName_TextChanged(object sender, EventArgs e)
        {
            btnOk.Enabled = OpenOkBtn();
        }
    }
}
