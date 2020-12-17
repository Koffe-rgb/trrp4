using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GodvilleClient.Model
{
    [Serializable]
    public class ClientMsg
    {
        // 0-результат дуэли, 1-конец хода, 2-запрос клиента на подключение, 3-глас клиента, 4-начало дуэли
        public int Type { get; set; }

        // номер хода (считает сервер)
        public int HodNum { get; set; }
        public string EnemyName { get; set; }

        // фраза о том, кто одержал победу или фраза о начале дуэли или фраза хода
        public string Phrase { get; set; }

        // 1-хорошо, 0-плохо
        public int Glas { get; set; }

        // колво жизней игрока (НЕ колво вычитаемой)
        public int Lives { get; set; }

        // колво жизней противника (НЕ колво вычитаемой)
        public int EnemyLives { get; set; }

        // таймер хода
        public int Time { get; set; }

        // ход клиента чет или нечет
        public bool IsEven { get; set; }

        // хроника событий
        public List<string> Chronicle { get; set; } = new List<string>();

        public ClientMsg()
        { }

        /**
         * Приветственное сообщение
         * @param hiPhrase приветственная фраза
         * @param lives колво жизней игрока
         * @param enemyLives колво жизней противника
         */
        public ClientMsg(string hiPhrase, int lives, int enemyLives, bool isEven)
        {
            Type = 4;
            HodNum = 0;
            Phrase = hiPhrase;
            Lives = lives;
            EnemyLives = enemyLives;
            IsEven = isEven;
            Time = 0;
        }

        /**
         * Сообщение конца хода или конца дуэли
         * @param type
         * @param hodNum
         * @param phrase
         * @param lives
         * @param enemyLives
         */
        public ClientMsg(int type, int hodNum, String phrase, int lives, int enemyLives)
        {
            Type = type;
            HodNum = hodNum;
            Phrase = phrase;
            Lives = lives;
            EnemyLives = enemyLives;
            Time = 0;
        }

        public void clear()
        {
            Type = -1;
            HodNum = -1;
            EnemyName = "";
            Phrase = "";
            Glas = -1;
            Lives = -1;
            EnemyLives = -1;
            IsEven = false;
            Time = -1;
        }
    }
}
