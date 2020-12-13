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
    public partial class StatisticForm : Form
    {
        List<string> enemyList;
        List<List<string>> duelHistoryList;
        public StatisticForm(List<string> enemyList, List<List<string>> duelHistoryList)
        {
            InitializeComponent();
            this.enemyList = enemyList;
            this.duelHistoryList = duelHistoryList;
            foreach (var enemy in enemyList)
                lvEnemy.Items.Add(enemy);
        }

        private void lvEnemy_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (lvEnemy.SelectedIndices.Count != 1)
                return;
            int index = lvEnemy.SelectedIndices[0];
            if (index == -1)
                return;
            lvDuelHistory.Items.Clear();
            var duel = duelHistoryList[index];
            foreach (var phrase in duel)
                lvDuelHistory.Items.Add(phrase);
        }
    }
}
