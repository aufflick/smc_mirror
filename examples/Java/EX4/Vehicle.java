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
// The Original Code is State Map Compiler (SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// Name
//  Vehicle
//
// Description
//  Every vehicle which appears on the canvas is an instance of
//  this class.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

public final class Vehicle
{
// Member methods.

    public Vehicle(Point startingPoint,
                   Point direction,
                   int speed,
                   Dimension size,
                   TrafficCanvas owner)
    {
        _position = new Point(startingPoint);
        _direction = new Point(direction);
        _speed = speed;
        _size = new Dimension(size);
        _owner = owner;

        _statemap = new VehicleContext(this);

        // Uncomment to see debug output.
        // _statemap.setDebugFlag(true);

        _statemap.Start();
    }

    public void setSpeed(int speed)
    {
        _speed = speed;
        return;
    }

    public void paint(Graphics2D g2)
    {
        draw(g2, Color.black);
        return;
    }

    public void move(Graphics2D g2)
    {
        _graphic = g2;
        _statemap.Move();
        return;
    }

    public void stopDemo(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        _statemap.Stop();

        // Cover over the vehicle's last position.
        draw(g2, Color.white);

        return;
    }

    public void lightGreen()
    {
        _statemap.LightGreen();
        return;
    }

    private void draw(Graphics2D g2, Paint color)
    {
        Point p1;
        Point p2;
        Point l1;
        Point l2;
        Point position;
        Dimension size;
        Dimension lightSize;
        int lightDirection;

        p1 = new Point(_position);
        p2 = new Point();
        position = new Point();
        size = new Dimension(_size);
        l1 = new Point();
        l2 = new Point();
        lightSize = new Dimension();

        // Check if the vehicle is moving through a
        // stoplight. Only paint that part of the
        // vehicle that is not under the stop light.

        // Get the stoplight's position, size and direction based
        // on the vehicle's direction.
        _owner.getLightDimensions(_direction,
                                  l1,
                                  lightSize);
        p2.x = _position.x + _size.width;
        p2.y = _position.y + _size.height;
        l2.x = l1.x + lightSize.width;
        l2.y = l1.y + lightSize.height;

        // Is the vehicle passing under the stoplight? It depends
        // on what direction this vehicle is moving.
        // For east and west, only check the x axis. For north
        // and south, only check the y axis.
        if ((_direction.x == _owner.EAST.x &&
             _direction.y == _owner.EAST.y) ||
            (_direction.x == _owner.WEST.x &&
             _direction.y == _owner.WEST.y))
        {
            // Is entire vehicle clear? It is if the vehicle's
            // left is to the light's right or the vehicle's
            // right is to the light's left or 
            if (p1.x > l2.x || p2.x < l1.x)
            {
                position.x = p1.x;
                position.y = p1.y;
                size.width = _size.width;
                size.height = _size.height;
            }
            // Is the vehicle's right side covered?
            else if (p1.x < l1.x)
            {
                // Yes. Figure out by how much.
                position.x = p1.x;
                position.y = p1.y;
                size.width = l1.x - p1.x - 1;
                size.height = _size.height;
            }
            // Is the vehicle's left side covered?
            else if (p2.x > l2.x)
            {
                position.x = l2.x + 1;
                position.y = p1.y;
                size.width = p2.x - l2.x - 1;
                size.height = _size.height;
            }
            else
            {
                // The vehicle is entirely covered.
                // Set size to 0.
                size.width = 0;
                size.height = 0;
            }
        }
        // Otherwise, we are heading north or south.
        else
        {
            // Check if the vehicle is entirely clear of the
            // stoplight.
            if (p1.y > l2.y || p2.y < l1.y)
            {
                position.x = p1.x;
                position.y = p1.y;
                size.width = _size.width;
                size.height = _size.height;
            }
            // Top part clear.
            else if (p1.y < l1.y)
            {
                position.x = p1.x;
                position.y = p1.y;
                size.width = _size.width;
                size.height = l1.y - p1.y - 1;
            }
            else if (p2.y > l2.y)
            {
                position.x = p1.x;
                position.y = l2.y + 1;
                size.width = _size.width;
                size.height = p2.y - l2.y - 1;
            }
            else
            {
                size.width = 0;
                size.height = 0;
            }
        }

        // Do the painting only if there is a rectangle to
        // paint, that is both the width and height are greater
        // than 0.
        if (size.width > 0 && size.height > 0)
        {
            g2.setPaint(color);
            g2.fill(new Rectangle2D.Double(position.getX(),
                                           position.getY(),
                                           size.getWidth(),
                                           size.getHeight()));
        }

        return;
    }

    //----------------------------------------
    // State Map Actions
    //
    // Guards
    public boolean TripDone()
    {
        Point newPosition = new Point(_position);
        boolean retval;

        // Figure out the vehicle's new position. If it is off
        // the canvas, tell the canvas of this vehicle's demise.
        newPosition.translate((_direction.x * _speed),
                               (_direction.y * _speed));

        if (newPosition.x < _owner.getMinX() ||
            newPosition.x > _owner.getMaxX() ||
            newPosition.y < _owner.getMinY() ||
            newPosition.y > _owner.getMaxY())
        {
            retval = true;
        }
        else
        {
            retval = false;
        }

        return(retval);
    }

    public boolean MayKeepGoing()
    {
        return(_owner.mayKeepGoing(_position,
                                   _speed,
                                   _direction));
    }

    public void WatchLight()
    {
        _owner.watchingLight(_direction, this);
        return;
    }

    public void EndTrip()
    {
        // Cover over the vehicle's last position.
        draw(_graphic, Color.white);
        _graphic = null;

        _owner.vehicleGone(this);

        return;
    }

    public void Advance()
    {
        int distance =
                _owner.getDistanceToIntersection(_position, _direction);
        Color lightColor =
                _owner.getLightsColor(_direction);
        int dx;
        int dy;

        // Cover over the vehicle's last position.
        draw(_graphic, Color.white);

        // Figure out the vehicle's new position.
        // If the light is red, then advance to intersection
        // and stop. Otherwise, keep going.
        if (lightColor == Color.red &&
            distance <= (_size.width + _speed))
        {
            dx = _direction.x * distance;
            dy = _direction.y * distance;
        }
        else
        {
            dx = _direction.x * _speed;
            dy = _direction.y * _speed;
        }

        _position.translate(dx, dy);

        // Draw the vehicle at its new location.
        draw(_graphic, Color.black);
        _graphic = null;

        return;
    }

// Member data.

    //----------------------------------------
    // Dynamic data.
    //
    // Where am I?
    private Point _position;

    // Where am I going?
    private Point _direction;

    // How fast am I going?
    private int _speed;

    // On what canvas do I appear?
    private TrafficCanvas _owner;

    // How large am I?
    private Dimension _size;

    // What am I doing?
    private VehicleContext _statemap;

    // Where am I being drawn?
    private Graphics2D _graphic;
}
