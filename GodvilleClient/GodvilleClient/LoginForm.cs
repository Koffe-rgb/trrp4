using System;
using System.Windows.Forms;

namespace GodvilleClient
{
    public partial class LoginForm : Form
    {
        Model.LoginData loginData;
        public LoginForm(Model.LoginData loginData)
        {
            InitializeComponent();
            this.loginData = loginData;
            txtLogin.Text = loginData.Login;
            txtPassword.Text = loginData.Password;
            btnOk.Enabled = OpenOkBtn();
        }

        private void btnOk_Click(object sender, EventArgs e)
        {
            loginData.Login = txtLogin.Text;
            loginData.Password = txtPassword.Text;
        }

        bool OpenOkBtn()
        {
            return !string.IsNullOrEmpty(txtLogin.Text) && !string.IsNullOrEmpty(txtPassword.Text);
        }

        private void awayLink_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            DialogResult = DialogResult.Cancel;
        }

        private void registerLink_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            DialogResult = DialogResult.Ignore;
        }

        private void txtLogin_TextChanged(object sender, EventArgs e)
        {
            btnOk.Enabled = OpenOkBtn();
        }

        private void txtPassword_TextChanged(object sender, EventArgs e)
        {
            btnOk.Enabled = OpenOkBtn();
        }
    }
}
