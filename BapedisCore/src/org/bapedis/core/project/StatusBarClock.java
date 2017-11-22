/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.project;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
//@ServiceProvider(service = StatusLineElementProvider.class)
public class StatusBarClock implements StatusLineElementProvider {

    public static final DateFormat format = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    private static JLabel time = new JLabel(" " + format.format(new Date()) + " ");
    private JPanel panel = new JPanel(new BorderLayout());

    public StatusBarClock() {
        Timer t = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                time.setText(" " + format.format(new Date()) + " ");
            }
        });
        t.start();
        panel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        panel.add(time, BorderLayout.CENTER);
    }

    @Override
    public Component getStatusLineElement() {
        return panel;
    }
    
    public static String getStrTime(){
        return format.format(new Date());
    }

}
