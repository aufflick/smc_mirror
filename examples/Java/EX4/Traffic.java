//
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// The Original Code is State Machine Compiler (SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// Name
//  Traffic.java
//
// Description
//  Creates the main frame and starts the demo running.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.3  2002/02/19 19:52:46  cwrapp
// Changes in release 1.3.0:
// Add the following features:
// + 479555: Added subroutine/method calls as argument types.
// + 508878: Added %import keyword.
//
// Revision 1.2  2001/12/14 20:10:37  cwrapp
// Changes in release 1.1.0:
// Add the following features:
// + 486786: Added the %package keyword which specifies the
//           Java package/C++ namespace/Tcl namespace
//           the SMC-generated classes will be placed.
// + 486471: The %class keyword accepts fully qualified
//           class names.
// + 491135: Add FSMContext methods getDebugStream and
//           setDebugStream.
// + 492165: Added -sync command line option which causes
//           the transition methods to be synchronized
//           (this option may only be used with -java).
//
// Revision 1.1.1.2  2001/03/26 14:41:47  cwrapp
// Corrected Entry/Exit action semantics. Exit actions are now
// executed only by simple transitions and pop transitions.
// Entry actions are executed by simple transitions and push
// transitions. Loopback transitions do not execute either Exit
// actions or entry actions. See SMC Programmer's manual for
// more information.
//
// Revision 1.1.1.1  2001/01/03 03:14:00  cwrapp
//
// ----------------------------------------------------------------------
// SMC - The State Map Compiler
// Version: 1.0, Beta 3
//
// SMC compiles state map descriptions into a target object oriented
// language. Currently supported languages are: C++, Java and [incr Tcl].
// SMC finite state machines have such features as:
// + Entry/Exit actions for states.
// + Transition guards
// + Transition arguments
// + Push and Pop transitions.
// + Default transitions. 
// ----------------------------------------------------------------------
//
// Revision 1.1.1.1  2000/08/02 12:51:02  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

package smc_ex4;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public final class Traffic
{
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) { }

        // Create the top-level container and add contents to it.
        JFrame frame = new JFrame("Stoplight Demo");
        Traffic app = new Traffic();
        Component contents = app.createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        //Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

// Member functions.
    public Traffic() {}

    public Component createComponents()
    {
        // Paint the traffic demo on this canvas.
        _trafficCanvas = new TrafficCanvas();

        // Put the canvas into a separate pane.
        JPanel canvasPane = new JPanel();
        canvasPane.setBorder(BorderFactory.createRaisedBevelBorder());
        canvasPane.add(_trafficCanvas);

        // Create the configuration dialog.
        _configDialog = new ConfigDialog(_trafficCanvas);

        // This button causes the configuration dialogue to be
        // displayed.
        _configButton = new JButton("Configure...");
        _configButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _configDialog.activate();
            }
        });
        _configButton.setEnabled(true);

        // This button starts the demo running.
        _startButton = new JButton("Start");
        _startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Have the canvas do its thing.
                _trafficCanvas.startDemo();

                // Now enable the pause and stop buttons and
                // disable the start button.
                _startButton.setEnabled(false);
                _pauseButton.setEnabled(true);
                _stopButton.setEnabled(true);
            }
        });
        _startButton.setEnabled(true);

        // This button pauses the demo.
        _pauseButton = new JButton("Pause");
        _pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Tell the canvas to pause its demo.
                _trafficCanvas.pauseDemo();

                // Now enable the continue button and
                // disable the pause button.
                _pauseButton.setEnabled(false);
                _continueButton.setEnabled(true);
            }
        });
        _pauseButton.setEnabled(false);

        // This button continues the paused demo.
        _continueButton = new JButton("Continue");
        _continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _trafficCanvas.continueDemo();

                // Enable the pause button and disable the
                // continue button.
                _continueButton.setEnabled(false);
                _pauseButton.setEnabled(true);
            }
        });
        _continueButton.setEnabled(false);

        // This buttons stops the demo.
        _stopButton = new JButton("Stop");
        _stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _trafficCanvas.stopDemo();

                // Enable the start button and disable the pause
                // and stop buttons.
                _stopButton.setEnabled(false);
                _pauseButton.setEnabled(false);
                _continueButton.setEnabled(false);
                _startButton.setEnabled(true);
            }
        });
        _stopButton.setEnabled(false);

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gridConstraints = new GridBagConstraints();

        JPanel pane = new JPanel();
        pane.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        30, //bottom
                                        30) //right
                                        );
        pane.setLayout(gridbag);

        // Set the button's grid constrains and then add it to
        // the pane.
        gridConstraints.anchor = GridBagConstraints.NORTH;
        gridConstraints.gridx = 3;
        gridConstraints.gridy = 0;
        gridConstraints.gridwidth = 1;
        gridbag.setConstraints(_configButton, gridConstraints);
        pane.add(_configButton);

        gridConstraints.anchor = GridBagConstraints.CENTER;
        gridConstraints.fill = GridBagConstraints.BOTH;
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 1;
        gridConstraints.gridwidth = 4;
        gridbag.setConstraints(canvasPane, gridConstraints);
        pane.add(canvasPane);

        gridConstraints.anchor = GridBagConstraints.SOUTH;
        gridConstraints.fill = GridBagConstraints.NONE;
        gridConstraints.gridwidth = 1;
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 2;
        gridbag.setConstraints(_startButton, gridConstraints);
        pane.add(_startButton);

        gridConstraints.gridx = 1;
        gridConstraints.gridy = 2;
        gridbag.setConstraints(_pauseButton, gridConstraints);
        pane.add(_pauseButton);

        gridConstraints.gridx = 2;
        gridConstraints.gridy = 2;
        gridbag.setConstraints(_continueButton, gridConstraints);
        pane.add(_continueButton);

        gridConstraints.gridx = 3;
        gridConstraints.gridy = 2;
        gridbag.setConstraints(_stopButton, gridConstraints);
        pane.add(_stopButton);

        return(pane);
    }

    private void resetGridContraints(GridBagConstraints constraints)
    {
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;

        return;
    }

// Member data.
    private TrafficCanvas _trafficCanvas;
    private ConfigDialog _configDialog;
    private JButton _configButton;
    private JButton _startButton;
    private JButton _pauseButton;
    private JButton _continueButton;
    private JButton _stopButton;
}
