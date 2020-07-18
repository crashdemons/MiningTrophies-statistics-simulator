/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.phsim;

import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class SetTestingEvent extends ServerEvent{
    public boolean state;
    private static final HandlerList HANDLERS = new HandlerList();
    public SetTestingEvent(boolean state){
        super();
        this.state=state;
    }
    

    /**
     * Get a list of handlers for the event.
     *
     * @return a list of handlers for the event
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Get a list of handlers for the event.
     *
     * @return a list of handlers for the event
     */
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
