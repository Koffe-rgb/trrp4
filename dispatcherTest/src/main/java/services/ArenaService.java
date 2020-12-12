package services;

import classes.Player;
import com.sun.xml.internal.ws.resources.ClientMessages;
import javafx.util.Pair;
import msg.ClientMsg;
import msg.DispatcherMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArenaService implements Runnable{
    private int id;
    private CopyOnWriteArrayList<Pair<String, Integer>> arenaServerIPs;
    private static int idPl = 0;
    Socket socket;

    public ArenaService(int id, CopyOnWriteArrayList<Pair<String, Integer>> arenaServerIPs) {
        this.arenaServerIPs = arenaServerIPs;
        this.id = id;
    }

    @Override
    public void run() {
        idPl++;
        System.out.println("Запускаем поток для очередного клиента");
        sendToServer();
    }


    private void sendToServer(){
        int arenaServer = 0;
        List<Pair<Integer, Integer>> clientsNums = new LinkedList<>();

        String ip;
        int port;
        ip = arenaServerIPs.get(arenaServer).getKey();
        port = arenaServerIPs.get(arenaServer).getValue();

        System.out.println("[x] Сервер арены: "+ip+ " "+ port);

        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                socket.setSoTimeout(60 * 1000);     // ждем ответа минуту
//                oos.writeObject(new DispatcherMsg(new Player(), -1,"How many clients?"));
                oos.writeInt(1);
                oos.flush();
//                boolean msg = ois.readBoolean();
//                DispatcherMsg respond = (DispatcherMsg) ois.readObject();   // получаем колво клиентов
//                clientsNums.add(new Pair<>(arenaServer,respond.getRespond()));

//                System.out.println("[x] У диспетчера "+ip+" "+port+" - "+ respond.getRespond()+" клиентов");
                System.out.println("[x] У диспетчера "+ip+" "+port+" - "+ " клиентов");
                while (true){
                    try {
                        int n = ois.readInt();
                        System.out.println(n);
                        if(n==2) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }


            } catch (IOException e) {
                System.out.println("[x] Ошибка подключения к серверу арены");
                e.printStackTrace();
                clientsNums.add(new Pair<>(arenaServer,-1));    // если не получили ответа
            }
            try {
                System.out.println("[x] Closing...");
                if (oos!=null) oos.close();
                if (ois!=null) ois.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
