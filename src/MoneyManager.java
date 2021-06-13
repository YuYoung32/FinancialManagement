import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MoneyManager {
    public static void main(String[] args) {
        new SqlFunction();                                                   //初始化数据库连接
        LoginFrame lf = new LoginFrame();
        lf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

//登录界面
class LoginFrame extends JFrame implements ActionListener {                  //标签一般直接new，后续还要有操作的先定义
    private final JTextField t_user = new JTextField(31);            //用户名文本框
    private final JPasswordField t_pwd = new JPasswordField(31);     //密码文本框
    private final JButton b_ok = new JButton("登录");
    private final JButton b_cancel = new JButton("退出");                //登录按钮，退出按钮

    /**
     * 设置框架
     */
    public LoginFrame() {                                                    //含参构造方法用于添加组件和传感器,设置标题-设置布局方式-添加按钮（创建对象）-添加监听-设置显示和其他
        super("欢迎使用个人理财账本!");                                    //设置标题，JFrame的构造方法，传入String即是标题
        //TODO: 改变字体大小会更美观
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));        //布局方式FlowLayout，一行排满排下一行
        add(new JLabel("用户名：", JLabel.RIGHT));
        add(t_user);
        add(new JLabel("密    码：", JLabel.RIGHT));
        add(t_pwd);
        add(b_ok);
        add(b_cancel);
        //为按钮添加监听事件
        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);
        // TODO: 布局可以增加图片，更改布局
        //界面大小不可调整
        this.setResizable(false);
        this.setSize(455, 150);

        //界面显示居中,设置窗口出现位置
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (b_cancel == e.getSource()) {//判断事件是从哪里发生的
            //点击退出，退出程序
            System.out.println("已退出");
            System.exit(0);
        } else if (b_ok == e.getSource()) {
            String loginID = t_user.getText();
            String loginPassword = new String(t_pwd.getPassword());
            try {
                if (new SqlFunction().login(loginID, loginPassword)) {
                    new MainFrame(t_user.getText().trim());    //验证身份成功后显示主界面
                    this.setVisible(false);                    //而后界面消失
                } else                                        //验证失败显示警告
                    JOptionPane.showMessageDialog(null, "用户名密码出错", "警告", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
    }
}

//主界面
class MainFrame extends JFrame implements ActionListener {
    //region 变量声明
    private JMenuBar mb = new JMenuBar();
    private JMenu m_system = new JMenu("系统管理");
    private JMenu m_fm = new JMenu("收支管理");
    private JMenuItem mI[] = {new JMenuItem("密码重置"), new JMenuItem("退出系统")};
    private JMenuItem m_FMEdit = new JMenuItem("收支编辑");
    private JLabel l_type, l_fromdate, l_todate, l_bal, l_ps;
    private JTextField t_fromdate, t_todate;
    private JButton b_select1, b_select2;
    private JComboBox c_type;
    private JPanel p_condition, p_detail;
    private String s1[] = {"收入", "支出"};
    private int bal1, bal2;
    private JTable table;
    private String username;
    private DefaultTableModel model;
    //endregion

    public MainFrame(String username) throws SQLException {    //构造方法
        //region 前端部分
        super(username + ",欢迎使用个人理财账本!");
        this.username = username;
        setLayout(new BorderLayout());    //布局方式 borderlayout 5个区域
        //设置目录
        add(mb, "North");        //添加并设置menubar位置，最上方north
        mb.add(m_system);                //目录-系统管理
        mb.add(m_fm);                    //目录-收支管理
        m_system.add(mI[0]);            //目录-系统管理-密码重置
        m_system.add(mI[1]);            //目录-系统管理-退出系统
        m_fm.add(m_FMEdit);                //目录-收支管理-收支编辑
        m_FMEdit.addActionListener(this);
        mI[0].addActionListener(this);
        mI[1].addActionListener(this);

        //设置查询面板
        l_type = new JLabel("收支类型：");
        c_type = new JComboBox(s1);                    //下拉框：收入、支出
        b_select1 = new JButton("查询");
        l_fromdate = new JLabel("起始时间");
        t_fromdate = new JTextField(8);
        l_todate = new JLabel("终止时间");
        t_todate = new JTextField(8);
        b_select2 = new JButton("查询");
        l_ps = new JLabel("注意：时间格式为YYYYMMDD，例如：20150901");

        p_condition = new JPanel();                                    //创建容器面板 p_condition
        p_condition.setLayout(new GridLayout(3, 1));        //设置布局方式 3行1列
        p_condition.setBorder(BorderFactory.createCompoundBorder(    //一个容器面板的相关项
                BorderFactory.createTitledBorder("输入查询条件"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.add(l_type);                            //文本：收支类型
        p1.add(c_type);                            //下拉框：收入支出
        p1.add(b_select1);                        //按钮：查询

        p2.add(l_fromdate);                        //文本：起始时间
        p2.add(t_fromdate);                        //输入框
        p2.add(l_todate);                        //文本：结束时间
        p2.add(t_todate);                        //输入框
        p2.add(b_select2);                        //按钮：查询

        p3.add(l_ps);                            //文本
        p_condition.add(p1);                    //把各个小的面板依次添加进容器面板
        p_condition.add(p2);
        p_condition.add(p3);
        add(p_condition, "Center");    //把容器面板添加入顶级容器

        b_select1.addActionListener(this);    //为两个查询按钮设置监听
        b_select2.addActionListener(this);

        //收支明细信息面板
        p_detail = new JPanel();
        p_detail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("收支明细信息"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        l_bal = new JLabel();
        //创建表格
        String[] cloum = {"编号", "日期", "类型", "内容", "金额",};
        Object[][] row = new Object[50][5];
        model = new DefaultTableModel(row, cloum) {
            public boolean isCellEditable(int rowIndex, int ColIndex) {                 //表格内容不可编辑
                return false;
            }
        };
        table = new JTable(model);
        //创建一个可以滑动的区域
        JScrollPane scrollpane = new JScrollPane(table);                                //创建一个可以滑动的区域
        scrollpane.setPreferredSize(new Dimension(580, 350));                //设置大小，超出大小即隐藏
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);    //设置竖直滑动条
        scrollpane.setViewportView(table);                                                //显示可滚动项

        p_detail.add(l_bal);                                                            //向容器内添加标签
        p_detail.add(scrollpane);                                                        //向容器内添加滑动区域
        add(p_detail, "South");                                                //向顶级容器内添加这个容器
        //endregion
        model.setRowCount(0);
        new SqlFunction().showData(model);
        bal1 =  new SqlFunction().returnMoney();
        if (bal1 < 0)
            l_bal.setText("个人总收支余额为" + bal1 + "元。您已超支，请适度消费！");
        else
            l_bal.setText("个人总收支余额为" + bal1 + "元。");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);                                                        //当前窗口不可缩放
        this.setSize(600, 580);                                                //设置窗口大小
        Dimension screen = this.getToolkit().getScreenSize();                            //设置出现位置在屏幕中央
        this.setLocation((screen.width - this.getSize().width) / 2, (screen.height - this.getSize().height) / 2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object temp = e.getSource();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try{
            if (temp == mI[0]) {                        //菜单项-密码重置
                new ModifyPwdFrame(username);           //修改密码界面
            } else if (temp == mI[1]) {                 //菜单项-退出系统
                System.out.println("已退出");
                System.exit(0);
            } else if (temp == m_FMEdit) {
                new BalEditFrame();                     //收支编辑界面
            } else if (temp == b_select1) {            //todo:该做这个了，根据收支类型查询
                String type = String.valueOf(c_type.getSelectedItem());
                String startDate = t_fromdate.getText();
                String endDate = t_todate.getText();
                try{
                     format.parse(startDate);
                }catch (ParseException p){
                    JOptionPane.showMessageDialog(null,                                    //弹出格式错误警告
                            "日期格式应为：YYYYMMDD", "警告", JOptionPane.ERROR_MESSAGE);
                }

                model.setRowCount(0);
                new SqlFunction().selShowData(model, startDate, endDate, type);
            } else if (temp == b_select2) {            //根据时间范围查询
                String type = (String) c_type.getSelectedItem();
                String startDate = t_fromdate.getText();
                String endDate = t_todate.getText();
                try{
                    format.parse(startDate);
                }catch (ParseException p){
                    JOptionPane.showMessageDialog(null,                                    //弹出格式错误警告
                            "日期格式应为：YYYYMMDD", "警告", JOptionPane.ERROR_MESSAGE);
                }
                model.setRowCount(0);
                new SqlFunction().selShowData(model, startDate, endDate, type);
            }
        }catch (SQLException s){
            s.printStackTrace();
        }

    }
}

//修改密码界面
class ModifyPwdFrame extends JFrame implements ActionListener {
    private JPasswordField t_oldPWD, t_newPWD, t_newPWDAgain;
    private JButton b_ok, b_cancel;
    private String username;

    /**
     * 设置框架
     *
     * @param username
     */
    public ModifyPwdFrame(String username) {
        super("修改密码");
        this.username = username;
        t_oldPWD = new JPasswordField(15);
        t_newPWD = new JPasswordField(15);
        t_newPWDAgain = new JPasswordField(15);
        b_ok = new JButton("确定");
        b_cancel = new JButton("取消");

        setLayout(new FlowLayout());
        add(new JLabel("旧密码："));
        add(t_oldPWD);
        add(new JLabel("新密码："));
        add(t_newPWD);
        add(new JLabel("确认新密码："));
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
            this.dispose();                                                                                //关闭当前窗体
        } else if (b_ok == e.getSource()) {                                                                //修改密码
            String t_oldPwd = new String(t_oldPWD.getPassword());
            String t_newPwd = new String(t_newPWD.getPassword());
            String t_newPwdAgain = new String(t_newPWDAgain.getPassword());
            //todo:可以实现实时监控的功能
            if (t_oldPwd.length() == 0 || t_newPwd.length() == 0 || t_newPwdAgain.length() == 0) {          //判断密码是否为空
                JOptionPane.showMessageDialog(null,                                         //弹出空密码警告
                        "请输入密码", "警告", JOptionPane.ERROR_MESSAGE);
            } else {
                //todo:或许可以用其他方式限制长度？
                if (t_newPwd.length() > 15 || t_newPwdAgain.length() > 15) {                               //判断密码长度
                    JOptionPane.showMessageDialog(null,                                    //弹出密码过长警告
                            "密码过长应小于15位", "警告", JOptionPane.ERROR_MESSAGE);
                } else {                                                                                   //密码输入格式合规，运行
                    try {
                        if (new SqlFunction().login(username, t_oldPwd)) {                                 //判断原密码是否正确
                            if (t_newPwd.equals(t_newPwdAgain)) {                                          //判断两次密码是否相同
                                new SqlFunction().changePwd(this.username, t_newPwd);                      //修改密码
                                this.dispose();
                            } else {
                                JOptionPane.showMessageDialog(null,                         //弹出密码不同警告
                                        "两次输入的密码不同", "警告", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "用户名密码出错",
                                    "警告", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException s) {
                        s.printStackTrace();
                    }
                }
            }
        }
    }
}


//收支编辑界面
class BalEditFrame extends JFrame implements ActionListener {
    private JTextField t_id, t_date, t_bal;
    private JComboBox c_type, c_item;
    private JButton b_update, b_delete, b_select, b_new, b_clear;
    private JPanel p1, p2, p3;
    private JScrollPane scrollpane;
    private DefaultTableModel model;
    private JTable table;
    private int selColIndex;

    public BalEditFrame() {
        super("收支编辑");
        t_id = new JTextField(8);
        t_date = new JTextField(8);
        t_bal = new JTextField(8);

        String[] s1 = {"收入", "支出"};
        String[] s2 = {"购物", "餐饮", "居家", "交通", "娱乐", "人情", "工资", "奖金", "其他"};
        c_type = new JComboBox(s1);                                                                 //下拉框
        c_item = new JComboBox(s2);

        b_new = new JButton("录入");
        b_update = new JButton("修改");
        b_delete = new JButton("删除");
        b_select = new JButton("查询");
        b_clear = new JButton("清空");

        setLayout(new BorderLayout());

        p1 = new JPanel();
        p1.setLayout(new GridLayout(5, 2, 10, 10));
        p1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("编辑收支信息"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        p1.add(new JLabel("编号："));
        p1.add(t_id);
        p1.add(new JLabel("日期："));
        p1.add(t_date);
        p1.add(new JLabel("类型："));
        p1.add(c_type);
        p1.add(new JLabel("内容："));
        p1.add(c_item);
        p1.add(new JLabel("金额："));
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
                BorderFactory.createTitledBorder("显示收支信息"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        String[] column = {"编号", "日期", "类型", "内容", "金额"};
        Object[][] row = new Object[50][5];
        model = new DefaultTableModel(row, column) {
            public boolean isCellEditable(int rowIndex, int ColIndex) {     //表格内容不可编辑
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

        b_new.addActionListener(this);          //录入
        b_update.addActionListener(this);       //修改
        b_delete.addActionListener(this);       //删除
        b_select.addActionListener(this);       //查询
        b_clear.addActionListener(this);        //清空
        table.addMouseListener(new MouseListener() {//为table添加鼠标点击事件监听addMouseListener
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {                                               //fixme:用户只能点击一次切换一次
                    int selectRows = table.getSelectedRows().length;                        //取得用户所选行的行数
                    if (selectRows == 1) {                                                  //一次只能选择一行
                        t_id.setText("");
                        t_date.setText("");
                        t_bal.setText("");
                        c_type.setSelectedIndex(0);
                        c_item.setSelectedIndex(0);

                        selColIndex = table.getSelectedRow();                               //取得用户所选行号
                        t_id.setText((String) table.getValueAt(selectRows, 0));     //把所选内容填入信息框
                        t_date.setText((String) table.getValueAt(selectRows, 1));
                        c_type.setSelectedItem(table.getValueAt(selectRows, 2));
                        c_item.setSelectedItem(table.getValueAt(selectRows, 3));
                        t_bal.setText(String.valueOf(table.getValueAt(selectRows, 4)));
                    }
                }
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
        try {
            if (b_select == e.getSource()) {            //查询所有收支信息
                model.setRowCount(0);                   //清空表格
                new SqlFunction().showData(model);
                table.setModel(model);
            } else if (b_update == e.getSource()) {     //修改某条收支信息
                String id = t_id.getText();
                String rdata = t_date.getText();
                String rtype = (String) c_type.getSelectedItem();
                String ritem = (String) c_item.getSelectedItem();
                int bal = Integer.parseInt(t_bal.getText());
                String selid = (String) table.getValueAt(selColIndex, 0);
                new SqlFunction().updateData(id, rdata, rtype, ritem, bal, selid);
            } else if (b_delete == e.getSource()) {     //删除某条收支信息
                String selid = (String) table.getValueAt(selColIndex, 0);
                new SqlFunction().deleteData(selid);
            } else if (b_new == e.getSource()) {        //新增某条收支信息
                String id = t_id.getText();
                String rdata = t_date.getText();
                String rtype = (String) c_type.getSelectedItem();
                String ritem = (String) c_item.getSelectedItem();
                int bal = Integer.parseInt(t_bal.getText());
                if (new SqlFunction().judgeRepeat(id)) {  //ID不能重复！
                    JOptionPane.showMessageDialog(null,
                            "ID已存在，请修改ID", "警告", JOptionPane.ERROR_MESSAGE);
                } else {
                    new SqlFunction().insertData(id, rdata, rtype, ritem, bal);
                }

            } else if (b_clear == e.getSource()) {      //清空输入框
                t_id.setText("");
                t_date.setText("");
                t_bal.setText("");
                c_type.setSelectedIndex(0);
                c_item.setSelectedIndex(0);
            }
            model.setRowCount(0);                       //每次操作都需要重新显示
            new SqlFunction().showData(model);
            table.setModel(model);
        } catch (SQLException s) {
            s.printStackTrace();
        }

    }
}
