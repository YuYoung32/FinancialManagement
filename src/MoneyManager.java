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

//��¼����
class LoginFrame extends JFrame implements ActionListener{
	private final JTextField t_user = new JTextField(31);			//�û����ı���
	private final JPasswordField t_pwd = new JPasswordField(31);	//�����ı���
	private final JButton b_ok = new JButton("��¼");
	private final JButton b_cancel =  new JButton("�˳�"); 				//��¼��ť���˳���ť

	public LoginFrame(){
		super("��ӭʹ�ø�������˱�!");									//���ñ��⣬JFrame�Ĺ��췽��������String���Ǳ���
		//TODO: �ı������С�������
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));		//���ַ�ʽFlowLayout��һ����������һ��
		add(new JLabel("�û�����",JLabel.RIGHT));
		add(t_user);
		add(new JLabel("��    �룺",JLabel.RIGHT));
		add(t_pwd);
		add(b_ok);
		add(b_cancel);
		//Ϊ��ť��Ӽ����¼�
		b_ok.addActionListener(this);
		b_cancel.addActionListener(this);
        // TODO: ���ֿ�������ͼƬ�����Ĳ���
		//�����С���ɵ���
		this.setResizable(false);
		this.setSize(455,150);
		
		//������ʾ����,���ô��ڳ���λ��
		Dimension screen = this.getToolkit().getScreenSize();
		this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(b_cancel==e.getSource()){//�ж��¼��Ǵ����﷢����
			//����˳����˳�����
			System.out.println("���˳�");
		    System.exit(0);
		}else if(b_ok==e.getSource()){
		    String loginID = t_user.getText();
			String loginPassword = new String(t_pwd.getPassword());
			try {
				if(new SqlFunction().login(loginID, loginPassword)){
					new MainFrame(t_user.getText().trim());	//��֤��ݳɹ�����ʾ������
				}
				else										//��֤ʧ����ʾ����
					JOptionPane.showMessageDialog(null,"�û����������", "����",JOptionPane.ERROR_MESSAGE);
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
	}
}

//������
class MainFrame extends JFrame implements ActionListener{
	//region ��������
	private JMenuBar mb=new JMenuBar();
	private JMenu m_system=new JMenu("ϵͳ����");
	private JMenu m_fm=new JMenu("��֧����");
	private JMenuItem mI[]={new JMenuItem("��������"),new JMenuItem("�˳�ϵͳ")};
	private JMenuItem m_FMEdit=new JMenuItem("��֧�༭");
	private JLabel l_type,l_fromdate,l_todate,l_bal,l_ps;  
	private JTextField t_fromdate,t_todate; 
	private JButton b_select1,b_select2;
	private JComboBox c_type;
	private JPanel p_condition,p_detail;
	private String s1[]={"����","֧��"};
	private double bal1,bal2;	
	private JTable table;
	private String username;
	//endregion
	
	public MainFrame(String username){	//���췽��
		//region ǰ�˲���
		super(username+",��ӭʹ�ø�������˱�!");
		this.username=username;
		setLayout(new BorderLayout());	//���ַ�ʽ borderlayout 5������
		//����Ŀ¼
		add(mb,"North");		//��Ӳ�����menubarλ�ã����Ϸ�north
		mb.add(m_system);				//Ŀ¼-ϵͳ����
		mb.add(m_fm);					//Ŀ¼-��֧����
		m_system.add(mI[0]);			//Ŀ¼-ϵͳ����-��������
		m_system.add(mI[1]);			//Ŀ¼-ϵͳ����-�˳�ϵͳ
		m_fm.add(m_FMEdit);				//Ŀ¼-��֧����-��֧�༭
		m_FMEdit.addActionListener(this);
		mI[0].addActionListener(this);
	    mI[1].addActionListener(this);

	    //���ò�ѯ���
	    l_type=new JLabel("��֧���ͣ�");	
	    c_type=new JComboBox(s1);					//���������롢֧��
	    b_select1=new JButton("��ѯ");
		l_fromdate=new JLabel("��ʼʱ��");
        t_fromdate=new JTextField(8);
		l_todate=new JLabel("��ֹʱ��");
        t_todate=new JTextField(8);
		b_select2=new JButton("��ѯ");
		l_ps = new JLabel("ע�⣺ʱ���ʽΪYYYYMMDD�����磺20150901");

		p_condition=new JPanel();									//����������� p_condition
		p_condition.setLayout(new GridLayout(3,1));		//���ò��ַ�ʽ 3��1��
		p_condition.setBorder(BorderFactory.createCompoundBorder(	//һ���������������
	    BorderFactory.createTitledBorder("�����ѯ����"), 
	    BorderFactory.createEmptyBorder(5,5,5,5)));
		
		JPanel p1 = new JPanel();
	    JPanel p2 = new JPanel();
	    JPanel p3 = new JPanel();
	    p1.add(l_type);							//�ı�����֧����
	    p1.add(c_type);							//����������֧��
	    p1.add(b_select1);						//��ť����ѯ

	    p2.add(l_fromdate);						//�ı�����ʼʱ��
		p2.add(t_fromdate);						//�����
		p2.add(l_todate);						//�ı�������ʱ��
		p2.add(t_todate);						//�����
		p2.add(b_select2);						//��ť����ѯ

		p3.add(l_ps);							//�ı�
		p_condition.add(p1);					//�Ѹ���С�����������ӽ��������
	    p_condition.add(p2);
	    p_condition.add(p3);
        add(p_condition,"Center");	//�������������붥������
        
        b_select1.addActionListener(this);	//Ϊ������ѯ��ť���ü���
        b_select2.addActionListener(this);

		//��֧��ϸ��Ϣ���
		p_detail=new JPanel();
        p_detail.setBorder(BorderFactory.createCompoundBorder(
	    BorderFactory.createTitledBorder("��֧��ϸ��Ϣ"), 
	    BorderFactory.createEmptyBorder(5,5,5,5)));
        l_bal=new JLabel();
		//�������
        String[] cloum = {"���", "����", "����","����","���",};
		Object[][] row = new Object[50][5];
		table = new JTable(row, cloum);
		//����һ�����Ի���������
		JScrollPane scrollpane = new JScrollPane(table);								//����һ�����Ի���������
		scrollpane.setPreferredSize(new Dimension(580,350));				//���ô�С��������С������
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//������ֱ������
		scrollpane.setViewportView(table);												//��ʾ�ɹ�����

		p_detail.add(l_bal);															//����������ӱ�ǩ
		p_detail.add(scrollpane);														//����������ӻ�������
		add(p_detail,"South");												//�򶥼�����������������
		//endregion
		//todo: ���ܲ��� ��ѯ�������ݣ���ֵ��ball�����ж�
		
	    if(bal1<0)
		    l_bal.setText("��������֧���Ϊ"+bal1+"Ԫ�����ѳ�֧�����ʶ����ѣ�");	
		else  		
			l_bal.setText("��������֧���Ϊ"+bal1+"Ԫ��");   				
		 	
        this.setResizable(false);														//��ǰ���ڲ�������
		this.setSize(600,580);												//���ô��ڴ�С
		Dimension screen = this.getToolkit().getScreenSize();							//���ó���λ������Ļ����
	    this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
		this.show();
	}
	
   public void actionPerformed(ActionEvent e) {
	     Object temp=e.getSource();
	     if(temp==mI[0]){
	    	new ModifyPwdFrame(username);
	     }else if(temp==mI[1]){
	    	//��Ӵ���
	     }else if(temp==m_FMEdit){
	    	new BalEditFrame();
	     }else if(temp==b_select1){  //������֧���Ͳ�ѯ	 
	    	//��Ӵ���
	     }else if(temp==b_select2){   //����ʱ�䷶Χ��ѯ		 
	    	//��Ӵ���
	     }
   }
}
//�޸��������
class ModifyPwdFrame extends JFrame implements ActionListener{
	private JLabel l_oldPWD,l_newPWD,l_newPWDAgain;
	private JPasswordField t_oldPWD,t_newPWD,t_newPWDAgain;
	private JButton b_ok,b_cancel;
	private String username;
	
	public ModifyPwdFrame(String username){
		super("�޸�����");
		this.username=username;
		l_oldPWD=new JLabel("������");
		l_newPWD=new JLabel("�����룺");
		l_newPWDAgain=new JLabel("ȷ�������룺");
		t_oldPWD=new JPasswordField(15);
		t_newPWD=new JPasswordField(15);
		t_newPWDAgain=new JPasswordField(15);
		b_ok=new JButton("ȷ��");
		b_cancel=new JButton("ȡ��");
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
			//��Ӵ���
		}else if(b_ok==e.getSource()){  //�޸�����
			//��Ӵ���
		}
	}
}
//��֧�༭����
class BalEditFrame extends JFrame implements ActionListener{
	private JLabel l_id,l_date,l_bal,l_type,l_item;
	private JTextField t_id,t_date,t_bal;
	private JComboBox c_type,c_item;
	private JButton b_update,b_delete,b_select,b_new,b_clear;
	private JPanel p1,p2,p3;
	private JScrollPane scrollpane;
	private JTable table;

	public BalEditFrame(){
		super("��֧�༭" );
		l_id=new JLabel("��ţ�");
		l_date=new JLabel("���ڣ�");
		l_bal=new JLabel("��");
		l_type=new JLabel("���ͣ�");
		l_item=new JLabel("���ݣ�");
		t_id=new JTextField(8);
		t_date=new JTextField(8);
		t_bal=new JTextField(8);

		String s1[]={"����","֧��"};
		String s2[]={"����","����","�Ӽ�","��ͨ","����","����","����","����","����"};
		c_type=new JComboBox(s1);
		c_item=new JComboBox(s2);
		
		b_select=new JButton("��ѯ");
		b_update=new JButton("�޸�");
		b_delete=new JButton("ɾ��");
		b_new=new JButton("¼��");
		b_clear=new JButton("���");
		
		Container c=this.getContentPane();
		c.setLayout(new BorderLayout());
		
		p1=new JPanel();
        p1.setLayout(new GridLayout(5,2,10,10));
        p1.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("�༭��֧��Ϣ"), 
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
		BorderFactory.createTitledBorder("��ʾ��֧��Ϣ"), 
		BorderFactory.createEmptyBorder(5,5,5,5)));		
				
		String[] cloum = { "���", "����", "����","����", "���"};
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
		
		//��Ӵ��룬Ϊtable���������¼�����addMouseListener
		
	    this.setResizable(false);
		this.setSize(800,300);
		Dimension screen = this.getToolkit().getScreenSize();
	    this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
		this.show();
	}
	public void actionPerformed(ActionEvent e) {
		if(b_select==e.getSource()){  //��ѯ������֧��Ϣ
			//��Ӵ���	 
		}else if(b_update==e.getSource()){  // �޸�ĳ����֧��Ϣ
			//��Ӵ���	
		}else if(b_delete==e.getSource()){   //ɾ��ĳ����֧��Ϣ
			//��Ӵ���	
		}else if(b_new==e.getSource()){   //����ĳ����֧��Ϣ 	
			//��Ӵ���	
		}else if(b_clear==e.getSource()){   //��������
			//��Ӵ���		 
		}	
	}
}
