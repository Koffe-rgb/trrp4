
namespace GodvilleClient
{
    partial class StatisticForm
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
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.lvEnemy = new System.Windows.Forms.ListView();
            this.lvDuelHistory = new System.Windows.Forms.ListView();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(107, 21);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(138, 28);
            this.label1.TabIndex = 0;
            this.label1.Text = "Противники";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(107, 292);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(174, 28);
            this.label2.TabIndex = 1;
            this.label2.Text = "История дуэли:";
            // 
            // lvEnemy
            // 
            this.lvEnemy.HideSelection = false;
            this.lvEnemy.Location = new System.Drawing.Point(107, 67);
            this.lvEnemy.MultiSelect = false;
            this.lvEnemy.Name = "lvEnemy";
            this.lvEnemy.Size = new System.Drawing.Size(592, 202);
            this.lvEnemy.TabIndex = 2;
            this.lvEnemy.UseCompatibleStateImageBehavior = false;
            this.lvEnemy.View = System.Windows.Forms.View.List;
            this.lvEnemy.SelectedIndexChanged += new System.EventHandler(this.lvEnemy_SelectedIndexChanged);
            // 
            // lvDuelHistory
            // 
            this.lvDuelHistory.HideSelection = false;
            this.lvDuelHistory.Location = new System.Drawing.Point(50, 345);
            this.lvDuelHistory.Name = "lvDuelHistory";
            this.lvDuelHistory.Size = new System.Drawing.Size(696, 251);
            this.lvDuelHistory.TabIndex = 3;
            this.lvDuelHistory.UseCompatibleStateImageBehavior = false;
            this.lvDuelHistory.View = System.Windows.Forms.View.List;
            // 
            // StatisticForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(13F, 28F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(800, 625);
            this.Controls.Add(this.lvDuelHistory);
            this.Controls.Add(this.lvEnemy);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Font = new System.Drawing.Font("Trebuchet MS", 13.8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point);
            this.Name = "StatisticForm";
            this.Text = "Статистика дуэлей";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.ListView lvEnemy;
        private System.Windows.Forms.ListView lvDuelHistory;
    }
}