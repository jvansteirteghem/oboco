package com.gitlab.jeeto.oboco.database;

import javax.inject.Inject;

import org.glassfish.hk2.api.Factory;

public class EntityManagerFactory implements Factory<javax.persistence.EntityManager> {
	@Inject
	private javax.persistence.EntityManagerFactory entityManagerFactory;
	
	@Override
	public javax.persistence.EntityManager provide() {
		return entityManagerFactory.createEntityManager();
	}
	
	@Override
	public void dispose(javax.persistence.EntityManager entityManager) {
		if(entityManager.isOpen()) {
            entityManager.close();
        }
	}
}
