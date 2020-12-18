
namespace GodvilleClient
{
    partial class StartForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(StartForm));
            this.lvDuelHistory = new System.Windows.Forms.ListView();
            this.btnGood = new System.Windows.Forms.Button();
            this.btnBad = new System.Windows.Forms.Button();
            this.btnGetStat = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.btnStartDuel = new System.Windows.Forms.Button();
            this.linkLogout = new System.Windows.Forms.LinkLabel();
            this.label2 = new System.Windows.Forms.Label();
            this.lblEnemyName = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.lblEnemyHealth = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.lblYourHealth = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.lblHeroName = new System.Windows.Forms.Label();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.SuspendLayout();
            // 
            // lvDuelHistory
            // 
            this.lvDuelHistory.HideSelection = false;
            this.lvDuelHistory.Location = new System.Drawing.Point(88, 229);
            this.lvDuelHistory.Name = "lvDuelHistory";
            this.lvDuelHistory.Size = new System.Drawing.Size(626, 370);
            this.lvDuelHistory.TabIndex = 2;
            this.lvDuelHistory.UseCompatibleStateImageBehavior = false;
            this.lvDuelHistory.View = System.Windows.Forms.View.List;
            // 
            // btnGood
            // 
            this.btnGood.Location = new System.Drawing.Point(93, 619);
            this.btnGood.Name = "btnGood";
            this.btnGood.Size = new System.Drawing.Size(247, 40);
            this.btnGood.TabIndex = 3;
            this.btnGood.Text = "Сделать хорошо";
            this.btnGood.UseVisualStyleBackColor = true;
            this.btnGood.Visible = false;
            this.btnGood.Click += new System.EventHandler(this.btnGood_Click);
            // 
            // btnBad
            // 
            this.btnBad.Location = new System.Drawing.Point(465, 619);
            this.btnBad.Name = "btnBad";
            this.btnBad.Size = new System.Drawing.Size(254, 40);
            this.btnBad.TabIndex = 4;
            this.btnBad.Text = "Сделать плохо";
            this.btnBad.UseVisualStyleBackColor = true;
            this.btnBad.Visible = false;
            this.btnBad.Click += new System.EventHandler(this.btnBad_Click);
            // 
            // btnGetStat
            // 
            this.btnGetStat.Location = new System.Drawing.Point(218, 669);
            this.btnGetStat.Name = "btnGetStat";
            this.btnGetStat.Size = new System.Drawing.Size(380, 49);
            this.btnGetStat.TabIndex = 5;
            this.btnGetStat.Text = "Посмотреть хроники";
            this.btnGetStat.UseVisualStyleBackColor = true;
            this.btnGetStat.Click += new System.EventHandler(this.btnGetStat_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Trebuchet MS", 18F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point);
            this.label1.Location = new System.Drawing.Point(279, 94);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(240, 38);
            this.label1.TabIndex = 6;
            this.label1.Text = "История дуэли";
            // 
            // btnStartDuel
            // 
            this.btnStartDuel.Location = new System.Drawing.Point(218, 619);
            this.btnStartDuel.Name = "btnStartDuel";
            this.btnStartDuel.Size = new System.Drawing.Size(380, 39);
            this.btnStartDuel.TabIndex = 7;
            this.btnStartDuel.Text = "Начать дуэль";
            this.btnStartDuel.UseVisualStyleBackColor = true;
            this.btnStartDuel.Click += new System.EventHandler(this.btnStartDuel_Click);
            // 
            // linkLogout
            // 
            this.linkLogout.AutoSize = true;
            this.linkLogout.Location = new System.Drawing.Point(571, 69);
            this.linkLogout.Name = "linkLogout";
            this.linkLogout.Size = new System.Drawing.Size(217, 28);
            this.linkLogout.TabIndex = 8;
            this.linkLogout.TabStop = true;
            this.linkLogout.Text = "Покинуть Годвилль";
            this.linkLogout.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLogout_LinkClicked);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(88, 144);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(180, 28);
            this.label2.TabIndex = 9;
            this.label2.Text = "Ваш противник:";
            // 
            // lblEnemyName
            // 
            this.lblEnemyName.AutoSize = true;
            this.lblEnemyName.Font = new System.Drawing.Font("Trebuchet MS", 13.8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point);
            this.lblEnemyName.Location = new System.Drawing.Point(274, 144);
            this.lblEnemyName.Name = "lblEnemyName";
            this.lblEnemyName.Size = new System.Drawing.Size(0, 28);
            this.lblEnemyName.TabIndex = 10;
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(400, 144);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(249, 28);
            this.label8.TabIndex = 11;
            this.label8.Text = "Здоровье противника:";
            // 
            // lblEnemyHealth
            // 
            this.lblEnemyHealth.AutoSize = true;
            this.lblEnemyHealth.Location = new System.Drawing.Point(647, 144);
            this.lblEnemyHealth.Name = "lblEnemyHealth";
            this.lblEnemyHealth.Size = new System.Drawing.Size(0, 28);
            this.lblEnemyHealth.TabIndex = 12;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(400, 185);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(179, 28);
            this.label3.TabIndex = 13;
            this.label3.Text = "Ваше здоровье:";
            // 
            // lblYourHealth
            // 
            this.lblYourHealth.AutoSize = true;
            this.lblYourHealth.Location = new System.Drawing.Point(585, 185);
            this.lblYourHealth.Name = "lblYourHealth";
            this.lblYourHealth.Size = new System.Drawing.Size(0, 28);
            this.lblYourHealth.TabIndex = 14;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(88, 185);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(130, 28);
            this.label5.TabIndex = 15;
            this.label5.Text = "Ваш герой:";
            // 
            // lblHeroName
            // 
            this.lblHeroName.AutoSize = true;
            this.lblHeroName.Location = new System.Drawing.Point(237, 185);
            this.lblHeroName.Name = "lblHeroName";
            this.lblHeroName.Size = new System.Drawing.Size(0, 28);
            this.lblHeroName.TabIndex = 16;
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
            this.pictureBox1.Location = new System.Drawing.Point(153, -1);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(515, 72);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox1.TabIndex = 17;
            this.pictureBox1.TabStop = false;
            // 
            // StartForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(13F, 28F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(800, 740);
            this.Controls.Add(this.pictureBox1);
            this.Controls.Add(this.lblHeroName);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.lblYourHealth);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.lblEnemyHealth);
            this.Controls.Add(this.label8);
            this.Controls.Add(this.lblEnemyName);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.linkLogout);
            this.Controls.Add(this.btnStartDuel);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.btnGetStat);
            this.Controls.Add(this.btnBad);
            this.Controls.Add(this.btnGood);
            this.Controls.Add(this.lvDuelHistory);
            this.Font = new System.Drawing.Font("Trebuchet MS", 13.8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point);
            this.Name = "StartForm";
            this.Text = "Добро пожаловать в Godville!";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.StartForm_FormClosing);
            this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.StartForm_FormClosed);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion
        private System.Windows.Forms.ListView lvDuelHistory;
        private System.Windows.Forms.Button btnGood;
        private System.Windows.Forms.Button btnBad;
        private System.Windows.Forms.Button btnGetStat;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button btnStartDuel;
        private System.Windows.Forms.LinkLabel linkLogout;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label lblEnemyName;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Label lblEnemyHealth;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label lblYourHealth;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label lblHeroName;
        private System.Windows.Forms.PictureBox pictureBox1;
    }
}