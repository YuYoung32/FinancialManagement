import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

import javax.swing.*;
import java.awt.event.*;

public class FrameWithButton1 extends JFrame {
    public FrameWithButton1(){
        super("Test");
        JButton button = new JButton("Button");
        button.addActionListener(e -> System.out.println());//button的事件监听器，触发时，执行xx操作//或者可以说一旦有活动就产生对象e
        add(button);
    }

    public static void main(String[] args) {
        FrameWithButton1 frame = new FrameWithButton1();//调用默认构造方法

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 100);
        frame.setVisible(true);
    }


}

