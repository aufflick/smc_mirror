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
-- Portions created by Charles W. Rapp are
-- Copyright (C) 2000 - 2003 Charles W. Rapp.
-- All Rights Reserved.
--
-- Contributor(s):
--       Port to Lua by Francois Perrad, francois.perrad@gadz.org
--
-- Function
--   Main
--
-- Description
--  This routine starts the finite state machine running.
--
-- RCS ID
-- $Id$
--
-- CHANGE LOG
-- $Log$
-- Revision 1.1  2007/01/03 15:40:45  fperrad
-- + Added Lua examples 1 - 3.
--
--
--

module(..., package.seeall)

require 'AppClass_sm'

function new (self)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o._fsm = AppClass_sm.AppClassContext:new({_owner = o})
    -- Uncomment to see debug output.
    -- o._fsm:setDebugFlag(true)
    return o
end

function CheckString (self, str)
    for c in string.gmatch(str, '.') do
        if c == '0' then
            self._fsm:Zero()
        elseif c == '1' then
            self._fsm:One()
        else
            self._fsm:Unknown()
        end
    end
    self._fsm:EOS()
    return self._is_acceptable
end

function Acceptable (self)
    self._is_acceptable = true
end

function Unacceptable (self)
    self._is_acceptable = false
end

