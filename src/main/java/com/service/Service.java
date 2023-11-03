package com.service;

import com.model.Entity;

/**
 * @author BeamStark
 * @date 2023-10-30-01:39
 */
public interface Service {

    void stopdo();

    Entity todo(Entity entity);
}
