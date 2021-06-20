import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PetScreen extends JFrame{
    private final JButton backButton;

    private ImageIcon[] images;
    private PetOperation myPetDog;

    private final JProgressBar hungerBar;
    private final JLabel hungerLabel;

    private final JProgressBar healthBar;
    private final JLabel healthLabel;

    private final JProgressBar thirstBar;
    private final JLabel thirstLabel;

    private JLabel pet;
    private String[] petState = {"normalState","hungryState","thirstyState","lowHpState"};

    private final JTextArea stateTextArea;
    private final JScrollPane stateScrollPane;


    private final JLabel userMoney;

    private buttonHandler buttoHandler = new buttonHandler();
    private final JButton useButton;
    private final JButton takeOffButton;
    private final JButton buyButton;

    private final JTabbedPane listsTabbedPane;

    private final JScrollPane storeListScrollPane;
    private JScrollPane bagScrollPane;

    private DefaultListModel bagModel;
    private JList userBagList;
    private final JList storeList;

    private String[] commodities = {"Water","Food","Medicine","Bowknot","Gold chain","Green scarf","Red scarf","Mask"};

    public PetScreen(){
        super("寵物系統");
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //創建照片
        images = new ImageIcon[commodities.length];
        for(int i=0;i<commodities.length;i++)
        {
            images[i] = createImageIcon(commodities[i] + ".png");
            Image tmpimage = images[i].getImage();
            Image resizingImage = tmpimage.getScaledInstance(100,100,Image.SCALE_DEFAULT);
            images[i] = new ImageIcon(resizingImage);
        }

        myPetDog = new PetOperation();

        backButton = new JButton("返回");
        backButton.setBounds(0,0,100,80);
        add(backButton);
        //創建飢餓條
        hungerBar = new JProgressBar(0,100);
        hungerBar.setForeground(Color.YELLOW);
        hungerBar.setValue(myPetDog.myDog.getHungerValue());
        hungerBar.setBounds(115,80,200,25);
        add(hungerBar);
        hungerLabel = new JLabel("飢餓度 " + myPetDog.myDog.getHungerValue() + "/100");
        hungerLabel.setFont(new Font("?",Font.BOLD + Font.ITALIC,20));
        hungerLabel.setBounds(330,80,200,25);
        add(hungerLabel);


        //創建血量條
        healthBar = new JProgressBar(0,1000);
        healthBar.setForeground(Color.red);
        healthBar.setValue(myPetDog.myDog.getHP());
        healthBar.setBounds(115,8,200,25);
        add(healthBar);
        healthLabel = new JLabel("生命值 " + myPetDog.myDog.getHP() + "/1000");
        healthLabel.setFont(new Font("生命值",Font.BOLD + Font.ITALIC,20));
        healthLabel.setBounds(330,8,200,25);
        add(healthLabel);

        //創建口渴條
        thirstBar = new JProgressBar(0,100);
        thirstBar.setForeground(Color.blue);
        thirstBar.setValue(myPetDog.myDog.getThirstValue());
        thirstBar.setBounds(115,45,200,25);
        add(thirstBar);
        thirstLabel = new JLabel("口渴度 " + myPetDog.myDog.getThirstValue() + "/100");
        thirstLabel.setFont(new Font("口渴值",Font.BOLD + Font.ITALIC,20));
        thirstLabel.setBounds(330,45,200,25);
        add(thirstLabel);

        //創建文字互動視窗
        stateTextArea = new JTextArea();
        stateScrollPane = new JScrollPane(stateTextArea);
        stateScrollPane.setBounds(50,400,650,100);
        add(stateScrollPane);

        //創建金錢標題
        userMoney = new JLabel("目前金錢 $:" + myPetDog.myBag.getMoney());
        userMoney.setFont(new Font("金錢",Font.BOLD + Font.ITALIC,20));
        userMoney.setBounds(550,500,350,50);
        add(userMoney);

        //創建互動按鈕
        buttoHandler = new buttonHandler();
        useButton = new JButton("使用");
        buyButton = new JButton("購買");
        takeOffButton = new JButton("脫下配件");
        takeOffButton.addActionListener(buttoHandler);
        useButton.addActionListener(buttoHandler);
        buyButton.addActionListener(buttoHandler);
        useButton.setBounds(50,500,100,50);
        takeOffButton.setBounds(150,500,100,50);
        buyButton.setBounds(450,500,100,50);
        takeOffButton.setEnabled(false);
        useButton.setEnabled(false);
        buyButton.setEnabled(false);
        add(takeOffButton);
        add(useButton);
        add(buyButton);

        //創建背包List
        userListHandler userHandler = new userListHandler();
        bagModel = new DefaultListModel();
        userBagList = new JList(bagModel);
        userBagList.setCellRenderer(new storeListRenderer());
        createBag();
        userBagList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userBagList.setLayoutOrientation(JList.VERTICAL);
        userBagList.setVisibleRowCount(-1);
        userBagList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        userBagList.addListSelectionListener(userHandler);
        bagScrollPane = new JScrollPane(userBagList);

        //創建商店List
        storeListHandler storeHandler = new storeListHandler();
        storeList = new JList(commodities);
        storeList.setCellRenderer(new storeListRenderer());
        storeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        storeList.setLayoutOrientation(JList.VERTICAL);
        storeList.setVisibleRowCount(-1);
        storeList.addListSelectionListener(storeHandler);
        storeListScrollPane = new JScrollPane(storeList);
        //storeList.setBounds(50,400,650,100);
        storeList.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //創建分頁的TabbedPane
        listsTabbedPane = new JTabbedPane(JTabbedPane.RIGHT);
        listsTabbedPane.addChangeListener(new windowChange());
        listsTabbedPane.addTab("背包",bagScrollPane);
        listsTabbedPane.addTab("商店",storeListScrollPane);
        listsTabbedPane.setBounds(495,100,250,300);// 50 400 650 100
        add(listsTabbedPane);

    }

    public JButton getBackButton(){
        return backButton;
    }

    //按鈕的事件處理器
    private class buttonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event)
        {

            if(event.getSource() == buyButton)
            {
                //購買事件發生
                System.out.println("購買成功!!");
                int index = storeList.getSelectedIndex();
                switch(index)
                {
                    case 0:
                        if(myPetDog.myBag.getMoney() < 5)
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"金錢不足!");
                        }
                        else {
                            myPetDog.myBag.setMoney(myPetDog.myBag.getMoney() - 5);
                            myPetDog.myBag.setWarter(myPetDog.myBag.getWater() + 1);
                            userMoney.setText("目前金錢 $:" + myPetDog.myBag.getMoney());
                            stateTextArea.append(String.format("水購買成功\n水數量:%d\n", myPetDog.myBag.getWater()));
                        }
                        break;
                    case 1:
                        if(myPetDog.myBag.getMoney() < 5)
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"金錢不足!");
                        }
                        else {
                            myPetDog.myBag.setMoney(myPetDog.myBag.getMoney() - 5);
                            myPetDog.myBag.setFood(myPetDog.myBag.getFood() + 1);
                            userMoney.setText("目前金錢 $:" + myPetDog.myBag.getMoney());
                            stateTextArea.append(String.format("食物購買成功\n食物數量:%d\n", myPetDog.myBag.getFood()));
                        }
                        break;
                    case 2:
                        if(myPetDog.myBag.getMoney() < 500)
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"金錢不足!");
                        }
                        else {
                            myPetDog.myBag.setMoney(myPetDog.myBag.getMoney() - 500);
                            myPetDog.myBag.setMedicine(myPetDog.myBag.getMedicine() + 1);
                            userMoney.setText("目前金錢 $:" + myPetDog.myBag.getMoney());
                            stateTextArea.append(String.format("復活藥購買成功\n復活藥數量:%d\n", myPetDog.myBag.getMedicine()));
                        }
                        break;
                    case 3:
                        if(myPetDog.myBag.getBowknot())
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"你已擁有蝴蝶結");
                        }
                        else
                        {
                            if(myPetDog.myBag.getMoney() < 1000)
                            {
                                JOptionPane.showMessageDialog(PetScreen.this,"金錢不足!");
                            }
                            else {
                                myPetDog.myBag.setMoney(myPetDog.myBag.getMoney() - 1000);
                                myPetDog.myBag.setBowknot(true);
                                userMoney.setText("目前金錢 $:" + myPetDog.myBag.getMoney());
                                stateTextArea.append("蝴蝶結購買成功\n");
                            }
                        }
                        break;
                    case 4:
                        if(myPetDog.myBag.getGoldChain())
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"你已擁有金項鍊");
                        }
                        else
                        {
                            if(myPetDog.myBag.getMoney() < 1000)
                            {
                                JOptionPane.showMessageDialog(PetScreen.this,"金錢不足!");
                            }
                            else {
                                myPetDog.myBag.setMoney(myPetDog.myBag.getMoney() - 1000);
                                myPetDog.myBag.setGoldChain(true);
                                userMoney.setText("目前金錢 $:" + myPetDog.myBag.getMoney());
                                stateTextArea.append("金項鍊購買成功\n");
                            }
                        }
                        break;
                    case 5:
                        if(myPetDog.myBag.getGreenScarf())
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"你已擁有綠領巾");
                        }
                        else
                        {
                            if(myPetDog.myBag.getMoney() < 1000)
                            {
                                JOptionPane.showMessageDialog(PetScreen.this,"金錢不足!");
                            }
                            else {
                                myPetDog.myBag.setGreenscarf(true);
                                myPetDog.myBag.setMoney(myPetDog.myBag.getMoney() - 1000);
                                userMoney.setText("目前金錢 $:" + myPetDog.myBag.getMoney());
                                stateTextArea.append("綠領巾購買成功\n");
                            }
                        }
                        break;
                    case 6:
                        if(myPetDog.myBag.getRedScarf())
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"你已擁有紅領巾");
                        }
                        else
                        {
                            if(myPetDog.myBag.getMoney() < 1000)
                            {
                                JOptionPane.showMessageDialog(PetScreen.this,"金錢不足!");
                            }
                            else {
                                myPetDog.myBag.setRedScarf(true);
                                myPetDog.myBag.setMoney(myPetDog.myBag.getMoney() - 1000);
                                userMoney.setText("目前金錢 $:" + myPetDog.myBag.getMoney());
                                stateTextArea.append("紅領巾購買成功\n");
                            }
                        }
                        break;
                    case 7:
                        if(myPetDog.myBag.getMask())
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"你已擁有口罩");
                        }
                        else
                        {
                            if(myPetDog.myBag.getMoney() < 1000)
                            {
                                JOptionPane.showMessageDialog(PetScreen.this,"金錢不足!");
                            }
                            else {
                                myPetDog.myBag.setMask(true);
                                myPetDog.myBag.setMoney(myPetDog.myBag.getMoney() - 1000);
                                userMoney.setText("目前金錢 $:" + myPetDog.myBag.getMoney());
                                stateTextArea.append("口罩購買成功\n");
                            }
                        }
                        break;
                }
                stateTextArea.setCaretPosition(stateTextArea.getDocument().getLength());
                myPetDog.writeBagDataCsv();
            }
            else if(event.getSource() == useButton)
            {
                //使用事件發生
                int index = userBagList.getSelectedIndex();
                switch (index)
                {
                    case 0:
                        if(myPetDog.myBag.getWater() > 0) {
                            if(myPetDog.myDog.getThirstValue() >= 100)
                            {
                                JOptionPane.showMessageDialog(PetScreen.this,"寵物不想再喝水了");
                            }
                            else {
                                myPetDog.myBag.setWarter(myPetDog.myBag.getWater() - 1);
                                myPetDog.myDog.setThirstValue(myPetDog.myDog.getThirstValue() + 5);
                                stateTextArea.append(String.format("水使用成功\n水數量:%d\n", myPetDog.myBag.getWater()));
                                thirstLabel.setText("口渴度 " + myPetDog.myDog.getThirstValue() + "/100");
                                thirstBar.setValue(myPetDog.myDog.getThirstValue());
                            }
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"沒有水可以使用了");
                        }
                        break;
                    case 1:
                        if(myPetDog.myBag.getFood() > 0) {
                            if(myPetDog.myDog.getHungerValue() >= 100)
                            {
                                JOptionPane.showMessageDialog(PetScreen.this,"寵物吃不下了!");
                            }
                            else {
                                myPetDog.myBag.setFood(myPetDog.myBag.getFood() - 1);
                                myPetDog.myDog.setHungerValue(myPetDog.myDog.getHungerValue() + 5);
                                stateTextArea.append(String.format("食物使用成功\n食物數量:%d\n", myPetDog.myBag.getFood()));
                                hungerLabel.setText("飢餓度 " + myPetDog.myDog.getHungerValue() + "/100");
                                hungerBar.setValue(myPetDog.myDog.getHungerValue());
                            }
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"沒有食物可以使用了");
                        }
                        break;
                    case 2:
                        if (myPetDog.myBag.getMedicine() > 0) {
                            if(myPetDog.myDog.getHP() <= 0) {
                                myPetDog.myBag.setMedicine(myPetDog.myBag.getMedicine() - 1);

                                stateTextArea.append(String.format("復活藥使用成功\n復活藥數量:%d\n", myPetDog.myBag.getMedicine()));
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(PetScreen.this, "寵物尚未死亡!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(PetScreen.this, "沒有復活藥可以使用了");
                        }

                        break;
                    case 3:
                        if(myPetDog.myBag.getBowknot())
                        {
                            if(myPetDog.myDog.getDecoration() != Decoration.bowknot)
                            {
                                if(myPetDog.myDog.getHP() <= 200)
                                {
                                    JOptionPane.showMessageDialog(PetScreen.this,"寵物狀態太差不想穿東西:(");
                                }
                                else
                                {
                                    myPetDog.myDog.setDecoration(Decoration.bowknot);
                                }
                            }
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"尚未購買蝴蝶結!");
                        }
                        break;
                    case 4:
                        if(myPetDog.myBag.getGoldChain())
                        {
                            if(myPetDog.myDog.getDecoration() != Decoration.goldChain)
                            {
                                if(myPetDog.myDog.getHP() <= 200)
                                {
                                    JOptionPane.showMessageDialog(PetScreen.this,"寵物狀態太差不想穿東西:(");
                                }
                                else
                                {
                                    myPetDog.myDog.setDecoration(Decoration.goldChain);
                                }
                            }
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"尚未購買金項鍊!");
                        }
                        break;
                    case 5:
                        if(myPetDog.myBag.getGreenScarf())
                        {
                            if(myPetDog.myDog.getDecoration() != Decoration.greenScarf)
                            {
                                if(myPetDog.myDog.getHP() <= 200)
                                {
                                    JOptionPane.showMessageDialog(PetScreen.this,"寵物狀態太差不想穿東西:(");
                                }
                                else
                                {
                                    myPetDog.myDog.setDecoration(Decoration.greenScarf);
                                }
                            }
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"尚未購買綠領巾!");
                        }
                        break;
                    case 6:
                        if(myPetDog.myBag.getRedScarf())
                        {
                            if(myPetDog.myDog.getDecoration() != Decoration.redScarf)
                            {
                                if(myPetDog.myDog.getHP() <= 200)
                                {
                                    JOptionPane.showMessageDialog(PetScreen.this,"寵物狀態太差不想穿東西:(");
                                }
                                else
                                {
                                    myPetDog.myDog.setDecoration(Decoration.redScarf);
                                }
                            }
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"尚未購買紅領巾!");
                        }
                        break;
                    case 7:
                        if (myPetDog.myBag.getMask())
                        {
                            if(myPetDog.myDog.getDecoration() != Decoration.mask)
                            {
                                if(myPetDog.myDog.getHP() <= 200)
                                {
                                    JOptionPane.showMessageDialog(PetScreen.this,"寵物狀態太差不想穿東西:(");
                                }
                                else
                                {
                                    myPetDog.myDog.setDecoration(Decoration.mask);
                                }
                            }
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(PetScreen.this,"尚未購買口罩!");
                        }
                        break;
                }
                stateTextArea.setCaretPosition(stateTextArea.getDocument().getLength());
                myPetDog.writeBagDataCsv();
                myPetDog.writePetDataCsv();
            }
        }
    }

    //背包的選擇事件處理器
    private class userListHandler implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent event)
        {
            if(event.getValueIsAdjusting() == false)
            {
                int index = userBagList.getSelectedIndex();
                if(index == -1)
                {
                    useButton.setEnabled(false);
                }
                else
                {
                    useButton.setEnabled(true);
                    System.out.println(bagModel.getElementAt(index));
                }
            }
        }
    }

    //商店的選擇事件處理器
    private class storeListHandler implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent event)
        {
            if(event.getValueIsAdjusting() == false)
            {
                int index = storeList.getSelectedIndex();
                if (storeList.getSelectedIndex() == -1) {
                    buyButton.setEnabled(false);
                } else {
                    buyButton.setEnabled(true);
                    System.out.println(commodities[index]);
                }
            }
        }
    }

    //處理LIST的CELL
    private class storeListRenderer extends DefaultListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus)
        {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            JLabel label = (JLabel) super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
            label.setIcon(images[index]);
            label.setHorizontalAlignment(JLabel.LEFT);

            return label;
        }
    }

    //當選擇另一個介面時把其中一個按鈕disabled  ((ex. 當選擇商店介面時，使用按鈕會被停用
    private class windowChange implements ChangeListener
    {
        @Override
        public void stateChanged(ChangeEvent event)
        {
            JTabbedPane tb = (JTabbedPane) event.getSource();
            int index = tb.getSelectedIndex();
            if(index == 0)
            {
                buyButton.setEnabled(false);
                storeList.clearSelection();
            }
            else
            {
                useButton.setEnabled(false);
                userBagList.clearSelection();
            }
        }
    }

    //創建背包
    public void createBag()
    {
        bagModel.addElement(commodities[0]);
        bagModel.addElement(commodities[1]);
        bagModel.addElement(commodities[2]);
        bagModel.addElement(commodities[3]);
        bagModel.addElement(commodities[4]);
        bagModel.addElement(commodities[5]);
        bagModel.addElement(commodities[6]);
        bagModel.addElement(commodities[7]);
    }

    //創建照片
    private ImageIcon createImageIcon(String path)
    {
        System.out.println(path);
        ImageIcon tmp = new ImageIcon("pet_Connected_Data/Pictures/" + path);
        return tmp;
    }

    public PetOperation getMyPetDog() { return myPetDog; }

    public JLabel getUserMoney() { return userMoney; }

}
