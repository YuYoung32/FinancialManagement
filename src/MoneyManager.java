import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class MoneyManager {
	public static void main(String[] args) {
		 LoginFrame lf=new LoginFrame();
		 lf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

//登录界面
class LoginFrame extends JFrame implements ActionListener{
	private final JTextField t_user = new JTextField(31);			//用户名文本框
	private final JPasswordField t_pwd = new JPasswordField(31);	//密码文本框
	private final JButton b_ok = new JButton("登录");
	private final JButton b_cancel =  new JButton("退出"); 				//登录按钮，退出按钮

	public LoginFrame(){
		super("欢迎使用个人理财账本!");									//设置标题，JFrame的构造方法，传入String即是标题
		//TODO: 改变字体大小会更美观
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));		//布局方式FlowLayout，一行排满排下一行
		add(new JLabel("用户名：",JLabel.RIGHT));
		add(t_user);
		add(new JLabel("密    码：",JLabel.RIGHT));
		add(t_pwd);
		add(b_ok);
		add(b_cancel);
		//为按钮添加监听事件
		b_ok.addActionListener(this);
		b_cancel.addActionListener(this);
        // TODO: 布局可以增加图片，更改布局
		//界面大小不可调整
		this.setResizable(false);
		this.setSize(455,150);
		
		//界面显示居中,设置窗口出现位置
		Dimension screen = this.getToolkit().getScreenSize();
		this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(b_cancel==e.getSource()){//判断事件是从哪里发生的
			//点击退出，退出程序
			System.out.println("已退出");
		    System.exit(0);
		}else if(b_ok==e.getSource()){
		    String loginID = t_user.getText();
			String loginPassword = new String(t_pwd.getPassword());
			try {
				if(new SqlFunction().login(loginID, loginPassword)){
					new MainFrame(t_user.getText().trim());	//验证身份成功后显示主界面
				}
				else										//验证失败显示警告
					JOptionPane.showMessageDialog(null,"用户名密码出错", "警告",JOptionPane.ERROR_MESSAGE);
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
	}
}

//主界面
class MainFrame extends JFrame implements ActionListener{
	//region 变量声明
	private JMenuBar mb=new JMenuBar();
	private JMenu m_system=new JMenu("系统管理");
	private JMenu m_fm=new JMenu("收支管理");
	private JMenuItem mI[]={new JMenuItem("密码重置"),new JMenuItem("退出系统")};
	private JMenuItem m_FMEdit=new JMenuItem("收支编辑");
	private JLabel l_type,l_fromdate,l_todate,l_bal,l_ps;  
	private JTextField t_fromdate,t_todate; 
	private JButton b_select1,b_select2;
	private JComboBox c_type;
	private JPanel p_condition,p_detail;
	private String s1[]={"收入","支出"};
	private double bal1,bal2;	
	private JTable table;
	private String username;
	//endregion
	
	public MainFrame(String username){	//构造方法
		//region 前端部分
		super(username+",欢迎使用个人理财账本!");
		this.username=username;
		setLayout(new BorderLayout());	//布局方式 borderlayout 5个区域
		//设置目录
		add(mb,"North");		//添加并设置menubar位置，最上方north
		mb.add(m_system);				//目录-系统管理
		mb.add(m_fm);					//目录-收支管理
		m_system.add(mI[0]);			//目录-系统管理-密码重置
		m_system.add(mI[1]);			//目录-系统管理-退出系统
		m_fm.add(m_FMEdit);				//目录-收支管理-收支编辑
		m_FMEdit.addActionListener(this);
		mI[0].addActionListener(this);
	    mI[1].addActionListener(this);

	    //设置查询面板
	    l_type=new JLabel("收支类型：");	
	    c_type=new JComboBox(s1);					//下拉框：收入、支出
	    b_select1=new JButton("查询");
		l_fromdate=new JLabel("起始时间");
        t_fromdate=new JTextField(8);
		l_todate=new JLabel("终止时间");
        t_todate=new JTextField(8);
		b_select2=new JButton("查询");
		l_ps = new JLabel("注意：时间格式为YYYYMMDD，例如：20150901");

		p_condition=new JPanel();									//创建容器面板 p_condition
		p_condition.setLayout(new GridLayout(3,1));		//设置布局方式 3行1列
		p_condition.setBorder(BorderFactory.createCompoundBorder(	//一个容器面板的相关项
	    BorderFactory.createTitledBorder("输入查询条件"), 
	    BorderFactory.createEmptyBorder(5,5,5,5)));
		
		JPanel p1 = new JPanel();
	    JPanel p2 = new JPanel();
	    JPanel p3 = new JPanel();
	    p1.add(l_type);							//文本：收支类型
	    p1.add(c_type);							//下拉框：收入支出
	    p1.add(b_select1);						//按钮：查询

	    p2.add(l_fromdate);						//文本：起始时间
		p2.add(t_fromdate);						//输入框
		p2.add(l_todate);						//文本：结束时间
		p2.add(t_todate);						//输入框
		p2.add(b_select2);						//按钮：查询

		p3.add(l_ps);							//文本
		p_condition.add(p1);					//把各个小的面板依次添加进容器面板
	    p_condition.add(p2);
	    p_condition.add(p3);
        add(p_condition,"Center");	//把容器面板添加入顶级容器
        
        b_select1.addActionListener(this);	//为两个查询按钮设置监听
        b_select2.addActionListener(this);

		//收支明细信息面板
		p_detail=new JPanel();
        p_detail.setBorder(BorderFactory.createCompoundBorder(
	    BorderFactory.createTitledBorder("收支明细信息"), 
	    BorderFactory.createEmptyBorder(5,5,5,5)));
        l_bal=new JLabel();
		//创建表格
        String[] cloum = {"编号", "日期", "类型","内容","金额",};
		Object[][] row = new Object[50][5];
		table = new JTable(row, cloum);
		//创建一个可以滑动的区域
		JScrollPane scrollpane = new JScrollPane(table);								//创建一个可以滑动的区域
		scrollpane.setPreferredSize(new Dimension(580,350));				//设置大小，超出大小即隐藏
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//设置竖直滑动条
		scrollpane.setViewportView(table);												//显示可滚动项

		p_detail.add(l_bal);															//向容器内添加标签
		p_detail.add(scrollpane);														//向容器内添加滑动区域
		add(p_detail,"South");												//向顶级容器内添加这个容器
		//endregion
		//todo: 功能部分 查询已有数据，赋值给ball进行判断
		
	    if(bal1<0)
		    l_bal.setText("个人总收支余额为"+bal1+"元。您已超支，请适度消费！");	
		else  		
			l_bal.setText("个人总收支余额为"+bal1+"元。");   				
		 	
        this.setResizable(false);														//当前窗口不可缩放
		this.setSize(600,580);												//设置窗口大小
		Dimension screen = this.getToolkit().getScreenSize();							//设置出现位置在屏幕中央
	    this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
		this.show();
	}
	
   public void actionPerformed(ActionEvent e) {
	     Object temp=e.getSource();
	     if(temp==mI[0]){
	    	new ModifyPwdFrame(username);
	     }else if(temp==mI[1]){
	    	//添加代码
	     }else if(temp==m_FMEdit){
	    	new BalEditFrame();
	     }else if(temp==b_select1){  //根据收支类型查询	 
	    	//添加代码
	     }else if(temp==b_select2){   //根据时间范围查询		 
	    	//添加代码
	     }
   }
}
//修改密码界面
class ModifyPwdFrame extends JFrame implements ActionListener{
	private JLabel l_oldPWD,l_newPWD,l_newPWDAgain;
	private JPasswordField t_oldPWD,t_newPWD,t_newPWDAgain;
	private JButton b_ok,b_cancel;
	private String username;
	
	public ModifyPwdFrame(String username){
		super("修改密码");
		this.username=username;
		l_oldPWD=new JLabel("旧密码");
		l_newPWD=new JLabel("新密码：");
		l_newPWDAgain=new JLabel("确认新密码：");
		t_oldPWD=new JPasswordField(15);
		t_newPWD=new JPasswordField(15);
		t_newPWDAgain=new JPasswordField(15);
		b_ok=new JButton("确定");
		b_cancel=new JButton("取消");
		Container c=this.getContentPane();
		c.setLayout(new FlowLayout());
		c.add(l_oldPWD);
		c.add(t_oldPWD);
		c.add(l_newPWD);
		c.add(t_newPWD);
		c.add(l_newPWDAgain);
		c.add(t_newPWDAgain);
		c.add(b_ok);
		c.add(b_cancel);
		b_ok.addActionListener(this);
		b_cancel.addActionListener(this);
		this.setResizable(false);
		this.setSize(280,160);
		Dimension screen = this.getToolkit().getScreenSize();
	    this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
		this.show();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(b_cancel==e.getSource()){
			//添加代码
		}else if(b_ok==e.getSource()){  //修改密码
			//添加代码
		}
	}
}
//收支编辑界面
class BalEditFrame extends JFrame implements ActionListener{
	private JLabel l_id,l_date,l_bal,l_type,l_item;
	private JTextField t_id,t_date,t_bal;
	private JComboBox c_type,c_item;
	private JButton b_update,b_delete,b_select,b_new,b_clear;
	private JPanel p1,p2,p3;
	private JScrollPane scrollpane;
	private JTable table;

	public BalEditFrame(){
		super("收支编辑" );
		l_id=new JLabel("编号：");
		l_date=new JLabel("日期：");
		l_bal=new JLabel("金额：");
		l_type=new JLabel("类型：");
		l_item=new JLabel("内容：");
		t_id=new JTextField(8);
		t_date=new JTextField(8);
		t_bal=new JTextField(8);

		String s1[]={"收入","支出"};
		String s2[]={"购物","餐饮","居家","交通","娱乐","人情","工资","奖金","其他"};
		c_type=new JComboBox(s1);
		c_item=new JComboBox(s2);
		
		b_select=new JButton("查询");
		b_update=new JButton("修改");
		b_delete=new JButton("删除");
		b_new=new JButton("录入");
		b_clear=new JButton("清空");
		
		Container c=this.getContentPane();
		c.setLayout(new BorderLayout());
		
		p1=new JPanel();
        p1.setLayout(new GridLayout(5,2,10,10));
        p1.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("编辑收支信息"), 
        BorderFactory.createEmptyBorder(5,5,5,5)));
		p1.add(l_id);
		p1.add(t_id);
		p1.add(l_date);
		p1.add(t_date);
		p1.add(l_type);
		p1.add(c_type);
		p1.add(l_item);
		p1.add(c_item);
		p1.add(l_bal);
		p1.add(t_bal);
		c.add(p1, BorderLayout.WEST);
		
		p2=new JPanel();
		p2.setLayout(new GridLayout(5,1,10,10));
		p2.add(b_new);
		p2.add(b_update);
		p2.add(b_delete);
		p2.add(b_select);
		p2.add(b_clear);
	    
		c.add(p2,BorderLayout.CENTER);	
		
		p3=new JPanel();
		p3.setBorder(BorderFactory.createCompoundBorder(
		BorderFactory.createTitledBorder("显示收支信息"), 
		BorderFactory.createEmptyBorder(5,5,5,5)));		
				
		String[] cloum = { "编号", "日期", "类型","内容", "金额"};
		Object[][] row = new Object[50][5];
		table = new JTable(row, cloum);
		scrollpane = new JScrollPane(table);
		scrollpane.setViewportView(table);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		p3.add(scrollpane);
		c.add(p3,BorderLayout.EAST);		
	 
		b_update.addActionListener(this);
		b_delete.addActionListener(this);
		b_select.addActionListener(this);
		b_new.addActionListener(this);
		b_clear.addActionListener(this);
		
		//添加代码，为table添加鼠标点击事件监听addMouseListener
		
	    this.setResizable(false);
		this.setSize(800,300);
		Dimension screen = this.getToolkit().getScreenSize();
	    this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
		this.show();
	}
	public void actionPerformed(ActionEvent e) {
		if(b_select==e.getSource()){  //查询所有收支信息
			//添加代码	 
		}else if(b_update==e.getSource()){  // 修改某条收支信息
			//添加代码	
		}else if(b_delete==e.getSource()){   //删除某条收支信息
			//添加代码	
		}else if(b_new==e.getSource()){   //新增某条收支信息 	
			//添加代码	
		}else if(b_clear==e.getSource()){   //清空输入框
			//添加代码		 
		}	
	}
}
