import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MoneyManager {
    public static void main(String[] args) {
        new SQLiteFunction();                                                   //��ʼ�����ݿ�����
        LoginFrame lf = new LoginFrame();
        lf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

//��¼����
class LoginFrame extends JFrame implements ActionListener {                  //��ǩһ��ֱ��new��������Ҫ�в������ȶ���
    private final JTextField t_user = new JTextField(31);            //�û����ı���
    private final JPasswordField t_pwd = new JPasswordField(31);     //�����ı���
    private final JButton b_ok = new JButton("��¼");
    private final JButton b_cancel = new JButton("�˳�");                //��¼��ť���˳���ť

    /**
     * ���ÿ��
     */
    public LoginFrame() {                                                    //���ι��췽�������������ʹ�����,���ñ���-���ò��ַ�ʽ-��Ӱ�ť����������-��Ӽ���-������ʾ������
        super("��ӭʹ�ø�������˱�!");                                    //���ñ��⣬JFrame�Ĺ��췽��������String���Ǳ���
        //TODO: �ı������С�������
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));        //���ַ�ʽFlowLayout��һ����������һ��
        add(new JLabel("�û�����", JLabel.RIGHT));
        add(t_user);
        add(new JLabel("��    �룺", JLabel.RIGHT));
        add(t_pwd);
        add(b_ok);
        add(b_cancel);
        //Ϊ��ť��Ӽ����¼�
        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);
        // TODO: ���ֿ�������ͼƬ�����Ĳ���
        //�����С���ɵ���
        this.setResizable(false);
        this.setSize(455, 150);

        //������ʾ����,���ô��ڳ���λ��
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (b_cancel == e.getSource()) {//�ж��¼��Ǵ����﷢����
            //����˳����˳�����
            System.out.println("���˳�");
            System.exit(0);
        } else if (b_ok == e.getSource()) {
            String loginID = t_user.getText();
            String loginPassword = new String(t_pwd.getPassword());
            try {
                if (new SQLiteFunction().login(loginID, loginPassword)) {
                    new MainFrame(t_user.getText().trim());    //��֤��ݳɹ�����ʾ������
                    this.setVisible(false);                    //���������ʧ
                } else                                        //��֤ʧ����ʾ����
                    JOptionPane.showMessageDialog(null, "�û����������", "����", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
    }
}

//������
class MainFrame extends JFrame implements ActionListener {
    //region ��������
    private final JMenuBar mb = new JMenuBar();
    private final JMenu m_system = new JMenu("ϵͳ����");
    private final JMenu m_fm = new JMenu("��֧����");
    private final JMenuItem[] mI = {new JMenuItem("��������"), new JMenuItem("�˳�ϵͳ")};
    private final JMenuItem m_FMEdit = new JMenuItem("��֧�༭");
    private final JLabel l_type;
    private final JLabel l_fromdate;
    private final JLabel l_todate;
    private final JLabel l_bal;
    private final JLabel l_ps;
    private final JTextField t_fromdate;
    private final JTextField t_todate;
    private final JButton b_select1;
    private final JButton b_select2;
    private final JComboBox<String> c_type;
    private final JPanel p_condition;
    private final JPanel p_detail;
    private final String[] s1 = {"����", "֧��"};
    private  int bal1;
    private final JTable table;
    private final String username;
    private final DefaultTableModel model;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    //endregion

    public MainFrame(String username) throws SQLException {    //���췽��
        //region ǰ�˲���
        super(username + ",��ӭʹ�ø�������˱�!");
        this.username = username;
        setLayout(new BorderLayout());    //���ַ�ʽ borderlayout 5������
        //����Ŀ¼
        add(mb, "North");        //��Ӳ�����menubarλ�ã����Ϸ�north
        mb.add(m_system);                //Ŀ¼-ϵͳ����
        mb.add(m_fm);                    //Ŀ¼-��֧����
        m_system.add(mI[0]);            //Ŀ¼-ϵͳ����-��������
        m_system.add(mI[1]);            //Ŀ¼-ϵͳ����-�˳�ϵͳ
        m_fm.add(m_FMEdit);                //Ŀ¼-��֧����-��֧�༭
        m_FMEdit.addActionListener(this);
        mI[0].addActionListener(this);
        mI[1].addActionListener(this);

        //���ò�ѯ���
        l_type = new JLabel("��֧���ͣ�");
        c_type = new JComboBox<>(s1);                    //���������롢֧��
        b_select1 = new JButton("��ѯ");
        l_fromdate = new JLabel("��ʼʱ��");
        t_fromdate = new JTextField(8);
        l_todate = new JLabel("��ֹʱ��");
        t_todate = new JTextField(8);
        b_select2 = new JButton("��ѯ");
        l_ps = new JLabel("ע�⣺ʱ���ʽΪYYYYMMDD�����磺20150901");

        p_condition = new JPanel();                                    //����������� p_condition
        p_condition.setLayout(new GridLayout(3, 1));        //���ò��ַ�ʽ 3��1��
        p_condition.setBorder(BorderFactory.createCompoundBorder(    //һ���������������
                BorderFactory.createTitledBorder("�����ѯ����"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.add(l_type);                            //�ı�����֧����
        p1.add(c_type);                            //����������֧��
        p1.add(b_select1);                        //��ť����ѯ

        p2.add(l_fromdate);                        //�ı�����ʼʱ��
        p2.add(t_fromdate);                        //�����
        p2.add(l_todate);                        //�ı�������ʱ��
        p2.add(t_todate);                        //�����
        p2.add(b_select2);                        //��ť����ѯ

        p3.add(l_ps);                            //�ı�
        p_condition.add(p1);                    //�Ѹ���С�����������ӽ��������
        p_condition.add(p2);
        p_condition.add(p3);
        add(p_condition, "Center");    //�������������붥������

        b_select1.addActionListener(this);    //Ϊ������ѯ��ť���ü���
        b_select2.addActionListener(this);

        //��֧��ϸ��Ϣ���
        p_detail = new JPanel();
        p_detail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("��֧��ϸ��Ϣ"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        l_bal = new JLabel();
        //�������
        String[] cloum = {"���", "����", "����", "����", "���",};
        Object[][] row = new Object[50][5];
        model = new DefaultTableModel(row, cloum) {
            public boolean isCellEditable(int rowIndex, int ColIndex) {                 //������ݲ��ɱ༭
                return false;
            }
        };
        table = new JTable(model);
        //����һ�����Ի���������
        JScrollPane scrollpane = new JScrollPane(table);                                //����һ�����Ի���������
        scrollpane.setPreferredSize(new Dimension(580, 350));                //���ô�С��������С������
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);    //������ֱ������
        scrollpane.setViewportView(table);                                                //��ʾ�ɹ�����

        p_detail.add(l_bal);                                                            //����������ӱ�ǩ
        p_detail.add(scrollpane);                                                        //����������ӻ�������
        add(p_detail, "South");                                                //�򶥼�����������������
        //endregion
        model.setRowCount(0);
        new SQLiteFunction().showData(model);
        bal1 =  new SQLiteFunction().returnMoney();
        if (bal1 < 0)
            l_bal.setText("��������֧���Ϊ" + bal1 + "Ԫ�����ѳ�֧�����ʶ����ѣ�");
        else
            l_bal.setText("��������֧���Ϊ" + bal1 + "Ԫ��");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);                                                        //��ǰ���ڲ�������
        this.setSize(600, 580);                                                //���ô��ڴ�С
        Dimension screen = this.getToolkit().getScreenSize();                            //���ó���λ������Ļ����
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object temp = e.getSource();

        try{
            if (temp == mI[0]) {                        //�˵���-��������
                new ModifyPwdFrame(username);           //�޸��������
            } else if (temp == mI[1]) {                 //�˵���-�˳�ϵͳ
                System.out.println("���˳�");
                System.exit(0);
            } else if (temp == m_FMEdit) {
                new BalEditFrame();                     //��֧�༭����
            } else if (temp == b_select1) {             //�������Ͳ�ѯ
                String type = String.valueOf(c_type.getSelectedItem());
                String startDate = "";
                String endDate = "";
                model.setRowCount(0);
                new SQLiteFunction().selShowData(model, startDate, endDate, type);
            } else if (temp == b_select2) {            //����ʱ�䷶Χ��ѯ
                String type = (String) c_type.getSelectedItem();
                String startDate = t_fromdate.getText();
                String endDate = t_todate.getText();
                try{
                    format.parse(startDate);
                }catch (ParseException p){
                    JOptionPane.showMessageDialog(null,                                    //������ʽ���󾯸�
                            "���ڸ�ʽӦΪ��YYYYMMDD", "����", JOptionPane.ERROR_MESSAGE);
                }
                model.setRowCount(0);
                new SQLiteFunction().selShowData(model, startDate, endDate, type);
            }
        }catch (SQLException s){
            s.printStackTrace();
        }

    }
}

//�޸��������
class ModifyPwdFrame extends JFrame implements ActionListener {
    private final JPasswordField t_oldPWD;
    private final JPasswordField t_newPWD;
    private final JPasswordField t_newPWDAgain;
    private final JButton b_ok;
    private final JButton b_cancel;
    private final String username;

    public ModifyPwdFrame(String username) {
        super("�޸�����");
        this.username = username;
        t_oldPWD = new JPasswordField(15);
        t_newPWD = new JPasswordField(15);
        t_newPWDAgain = new JPasswordField(15);
        b_ok = new JButton("ȷ��");
        b_cancel = new JButton("ȡ��");

        setLayout(new FlowLayout());
        add(new JLabel("�����룺"));
        add(t_oldPWD);
        add(new JLabel("�����룺"));
        add(t_newPWD);
        add(new JLabel("ȷ�������룺"));
        add(t_newPWDAgain);
        add(b_ok);
        add(b_cancel);

        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);

        this.setResizable(false);
        this.setSize(280, 160);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (b_cancel == e.getSource()) {
            this.dispose();                                                                                //�رյ�ǰ����
        } else if (b_ok == e.getSource()) {                                                                //�޸�����
            String t_oldPwd = new String(t_oldPWD.getPassword());
            String t_newPwd = new String(t_newPWD.getPassword());
            String t_newPwdAgain = new String(t_newPWDAgain.getPassword());
            //todo:����ʵ��ʵʱ��صĹ���
            if (t_oldPwd.length() == 0 || t_newPwd.length() == 0 || t_newPwdAgain.length() == 0) {          //�ж������Ƿ�Ϊ��
                JOptionPane.showMessageDialog(null,                                         //���������뾯��
                        "����������", "����", JOptionPane.ERROR_MESSAGE);
            } else {
                //todo:���������������ʽ���Ƴ��ȣ�
                if (t_newPwd.length() > 15 || t_newPwdAgain.length() > 15) {                               //�ж����볤��
                    JOptionPane.showMessageDialog(null,                                    //���������������
                            "�������ӦС��15λ", "����", JOptionPane.ERROR_MESSAGE);
                } else {                                                                                   //���������ʽ�Ϲ棬����
                    try {
                        if (new SQLiteFunction().login(username, t_oldPwd)) {                                 //�ж�ԭ�����Ƿ���ȷ
                            if (t_newPwd.equals(t_newPwdAgain)) {                                          //�ж����������Ƿ���ͬ
                                new SQLiteFunction().changePwd(this.username, t_newPwd);                      //�޸�����
                                this.dispose();
                            } else {
                                JOptionPane.showMessageDialog(null,                         //�������벻ͬ����
                                        "������������벻ͬ", "����", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "�û����������",
                                    "����", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException s) {
                        s.printStackTrace();
                    }
                }
            }
        }
    }
}


//��֧�༭����
class BalEditFrame extends JFrame implements ActionListener {
    private final JTextField t_id;
    private final JTextField t_date;
    private final JTextField t_bal;
    private final JComboBox<String> c_type;
    private final JComboBox<String> c_item;
    private final JButton b_update;
    private final JButton b_delete;
    private final JButton b_select;
    private final JButton b_new;
    private final JButton b_clear;
    private final JPanel p1;
    private final JPanel p2;
    private final JPanel p3;
    private final JScrollPane scrollpane;
    private final DefaultTableModel model;
    private final JTable table;
    private int selRowIndex;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    public BalEditFrame() {
        super("��֧�༭");
        t_id = new JTextField(8);
        t_date = new JTextField(8);
        t_bal = new JTextField(8);

        String[] s1 = {"����", "֧��"};
        String[] s2 = {"����", "����", "�Ӽ�", "��ͨ", "����", "����", "����", "����", "����"};
        c_type = new JComboBox<>(s1);                                                                 //������
        c_item = new JComboBox<>(s2);

        b_new = new JButton("¼��");
        b_update = new JButton("�޸�");
        b_delete = new JButton("ɾ��");
        b_select = new JButton("��ѯ");
        b_clear = new JButton("���");

        setLayout(new BorderLayout());

        p1 = new JPanel();
        p1.setLayout(new GridLayout(5, 2, 10, 10));
        p1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("�༭��֧��Ϣ"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        p1.add(new JLabel("��ţ�"));
        p1.add(t_id);
        p1.add(new JLabel("���ڣ�"));
        p1.add(t_date);
        p1.add(new JLabel("���ͣ�"));
        p1.add(c_type);
        p1.add(new JLabel("���ݣ�"));
        p1.add(c_item);
        p1.add(new JLabel("��"));
        p1.add(t_bal);
        add(p1, BorderLayout.WEST);

        p2 = new JPanel();
        p2.setLayout(new GridLayout(5, 1, 10, 10));
        p2.add(b_new);
        p2.add(b_update);
        p2.add(b_delete);
        p2.add(b_select);
        p2.add(b_clear);
        add(p2, BorderLayout.CENTER);

        p3 = new JPanel();
        p3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("��ʾ��֧��Ϣ"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        String[] column = {"���", "����", "����", "����", "���"};
        Object[][] row = new Object[50][5];
        model = new DefaultTableModel(row, column) {
            public boolean isCellEditable(int rowIndex, int ColIndex) {     //������ݲ��ɱ༭
                return false;
            }
        };
        model.setRowCount(0);
        table = new JTable(model);
        scrollpane = new JScrollPane(table);
        scrollpane.setViewportView(table);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        p3.add(scrollpane);
        add(p3, BorderLayout.EAST);

        b_new.addActionListener(this);          //¼��
        b_update.addActionListener(this);       //�޸�
        b_delete.addActionListener(this);       //ɾ��
        b_select.addActionListener(this);       //��ѯ
        b_clear.addActionListener(this);        //���
        table.addMouseListener(new MouseListener() {//Ϊtable���������¼�����addMouseListener
            @Override
            public void mouseClicked(MouseEvent e) {
                        t_id.setText("");
                        t_date.setText("");
                        t_bal.setText("");
                        c_type.setSelectedIndex(0);
                        c_item.setSelectedIndex(0);

                        selRowIndex = table.getSelectedRow();                               //ȡ���û���ѡ�к�
                        t_id.setText((String) table.getValueAt(selRowIndex, 0));     //����ѡ����������Ϣ��
                        t_date.setText((String) table.getValueAt(selRowIndex, 1));
                        c_type.setSelectedItem(table.getValueAt(selRowIndex, 2));
                        c_item.setSelectedItem(table.getValueAt(selRowIndex, 3));
                        t_bal.setText(String.valueOf(table.getValueAt(selRowIndex, 4)));
                    }



            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        this.setResizable(false);
        this.setSize(800, 300);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        //todo ���ǰѱ���������ǰ�棬��С���볤��?
        try {
            if (b_select == e.getSource()) {            //��ѯ������֧��Ϣ
                model.setRowCount(0);                   //��ձ��
                new SQLiteFunction().showData(model);
                table.setModel(model);
            } else if (b_update == e.getSource()) {     //�޸�ĳ����֧��Ϣ
                String id = t_id.getText();
                String rdata = t_date.getText();
                String rtype = (String) c_type.getSelectedItem();
                String ritem = (String) c_item.getSelectedItem();
                int bal = Integer.parseInt(t_bal.getText());
                String selid = (String) table.getValueAt(selRowIndex, 0);       //ѡ��ĳ��
                if(new DataConfirm().isNotEmpty5(id, rdata, rtype, ritem, t_bal.getText()))
                    new SQLiteFunction().updateData(id, rdata, rtype, ritem, bal, selid);
                else
                    JOptionPane.showMessageDialog(null,                                    //������ʽ���󾯸�
                            "���������Ϊ��", "����", JOptionPane.ERROR_MESSAGE);
                try{
                    format.parse(rdata);
                } catch (ParseException p){
                    JOptionPane.showMessageDialog(null,                                    //������ʽ���󾯸�
                            "���ڸ�ʽӦΪ��YYYYMMDD", "����", JOptionPane.ERROR_MESSAGE);
                    new SQLiteFunction().deleteData(id);                                                       //ɾ����������
                }

            } else if (b_delete == e.getSource()) {     //ɾ��ĳ����֧��Ϣ
                String selid = (String) table.getValueAt(selRowIndex, 0);
                new SQLiteFunction().deleteData(selid);
            } else if (b_new == e.getSource()) {        //����ĳ����֧��Ϣ
                String id = t_id.getText();
                String rdata = t_date.getText();
                String rtype = (String) c_type.getSelectedItem();
                String ritem = (String) c_item.getSelectedItem();
                int bal = Integer.parseInt(t_bal.getText());
                if (!new DataConfirm().isNotEmpty5(id, rdata, rtype, ritem, t_bal.getText()))
                    JOptionPane.showMessageDialog(null,                                    //������ʽ���󾯸�
                            "���������Ϊ��", "����", JOptionPane.ERROR_MESSAGE);
                if (new SQLiteFunction().judgeRepeat(id)) {  //ID�����ظ���
                    JOptionPane.showMessageDialog(null,
                            "ID�Ѵ��ڣ����޸�ID", "����", JOptionPane.ERROR_MESSAGE);
                } else {
                    new SQLiteFunction().insertData(id, rdata, rtype, ritem, bal);
                }
                try{
                    format.parse(rdata);
                } catch (ParseException p){
                    JOptionPane.showMessageDialog(null,                                    //������ʽ���󾯸�
                            "���ڸ�ʽӦΪ��YYYYMMDD", "����", JOptionPane.ERROR_MESSAGE);
                    new SQLiteFunction().deleteData(id);                                                       //ɾ����������
                }


            } else if (b_clear == e.getSource()) {      //��������
                t_id.setText("");
                t_date.setText("");
                t_bal.setText("");
                c_type.setSelectedIndex(0);
                c_item.setSelectedIndex(0);
            }
            model.setRowCount(0);                       //ÿ�β�������Ҫ������ʾ
            new SQLiteFunction().showData(model);
            table.setModel(model);
        } catch (SQLException s) {
            s.printStackTrace();
        }

    }
}
