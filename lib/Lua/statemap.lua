--
-- The contents of this file are subject to the Mozilla Public
-- License Version 1.1 (the "License"); you may not use this file
-- except in compliance with the License. You may obtain a copy of
-- the License at http://www.mozilla.org/MPL/
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- The Original Code is State Machine Compiler (SMC).
--
-- The Initial Developer of the Original Code is Charles W. Rapp.
--
-- Port to Lua by Francois Perrad, francois.perrad@gadz.org
-- Copyright 2007, Francois Perrad.
-- All Rights Reserved.
--
-- Contributor(s):
--
-- RCS ID
-- $Id$
--
--
-- This module contains two class  :
-- * State
--    the base State class
-- * FSMContext
--    the Finite State Machine Context class
--
-- See: http://smc.sourceforge.net/
--

module(..., package.seeall)

-- base State class
State = {}

function State:class ()
    local o = {}
    setmetatable(o, self)
    self.__index = self
    return o
end

function State:new (name, id)
    o = {_name = name, _id = id}
    setmetatable(o, self)
    self.__index = self
    return o
end

function State:getName ()
    -- Returns the state's printable name.
    return self._name
end

function State:getId ()
    -- Returns the state's unique identifier.
    return self._id
end


--[[
 The user can derive FSM contexts from this class and interface
 to them with the methods of this class.

 The finite state machine needs to be initialized to the starting
 state of the FSM.  This must be done manually in the constructor
 of the derived class.
]]
FSMContext = {}

function FSMContext:class ()
    local o = {}
    setmetatable(o, self)
    self.__index = self
    return o
end

function FSMContext:new (o)
    o = o or {}
    o._state_stack = {}
    o._debug_stream = io.stderr
    setmetatable(o, self)
    self.__index = self
    o:_init()
    return o
end

function FSMContext:_init ()
    error "FSMContext can't be instantiated"
end

function FSMContext:getDebugFlag ()
    -- Returns the debug flag's current setting.
    return self._debug_flag
end

function FSMContext:setDebugFlag (flag)
    -- Sets the debug flag.
    --
    -- A true value means debugging is on and false means off.
    self._debug_flag = flag
end

function FSMContext:getDebugStream ()
    -- Returns the stream to which debug output is written.
    return self._debug_stream
end

function FSMContext:setDebugStream (stream)
    -- Sets the debug output stream.
    self._debug_stream = stream
end

function FSMContext:isInTransition ()
    -- Is this state machine already inside a transition?
    --
    -- True if state is undefined.
    return self._state == nil
end

function FSMContext:getTranstion ()
    -- Returns the current transition's name.
    --
    -- Used only for debugging purposes.
    return self._transition
end

function FSMContext:clearState ()
    -- Clears the current state.
    self._previous_state = self._state
    self._state = nil
end

function FSMContext:getPreviousState ()
    -- Returns the state which a transition left.
    --
    -- May be nil.
    return self._previous_state
end

function FSMContext:setState (state)
    -- Sets the current state to the specified state.
    assert(state ~= nil, "undefined state.")
    assert(type(state) == 'table') -- "state should be a State"
    self._state = state
    if self._debug_flag then
        self._debug_stream:write("NEW STATE    : ", self._state:getName(), "\n")
    end
end

function FSMContext:isStateStackEmpty ()
    -- Returns true if the state stack is empty and false otherwise.
    return #self._state_stack == 0
end

function FSMContext:getStackDepth ()
    -- Returns the state stack's depth.
    return #self._state_stack
end

function FSMContext:pushState (state)
    -- Push the current state on top of the state stack
    -- and make the specified state the current state.
    assert(state ~= nil, "undefined state.")
    assert(type(state) == 'table') -- "state should be a State"
    if self._state then
        table.insert(self._state_stack, self._state)
    end
    self._state = state
    if self._debug_flag then
        self._debug_stream:write("PUSH TO STATE: ", self._state:getName(), "\n")
    end
end

function FSMContext:popState ()
    -- Make the state on top of the state stack the current state.
    if #self._state_stack == 0 then
        if self._debug_flag then
            self._debug_stream:write("POPPING ON EMPTY STATE STACK.\n")
        end
        error("empty state stack.")
    else
        self._state = table.remove(self._state_stack)
        if self._debug_flag then
            self._debug_stream:write("POP TO STATE: ", self._state:getName(), "\n")
        end
    end
end

function FSMContext:emptyStateStack ()
    -- Remove all states from the state stack.
    self._state_stack = {}
end

