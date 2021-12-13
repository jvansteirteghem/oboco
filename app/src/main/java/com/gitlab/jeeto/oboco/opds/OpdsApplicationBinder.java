package com.gitlab.jeeto.oboco.opds;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

import com.gitlab.jeeto.oboco.database.EntityManagerFactory;
import com.gitlab.jeeto.oboco.database.EntityManagerFactoryFactory;
import com.gitlab.jeeto.oboco.database.book.BookService;
import com.gitlab.jeeto.oboco.database.bookcollection.BookCollectionService;
import com.gitlab.jeeto.oboco.database.user.UserService;
import com.gitlab.jeeto.oboco.server.authentication.UserTokenService;

public class OpdsApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
    	bindFactory(EntityManagerFactoryFactory.class).proxy(true).proxyForSameScope(false).to(javax.persistence.EntityManagerFactory.class).in(Singleton.class);
    	bindFactory(EntityManagerFactory.class).proxy(true).proxyForSameScope(false).to(javax.persistence.EntityManager.class).in(RequestScoped.class);
    	bind(BookService.class).to(BookService.class);
    	bind(BookCollectionService.class).to(BookCollectionService.class);
    	bind(UserService.class).to(UserService.class);
    	bind(UserTokenService.class).to(UserTokenService.class);
    }
}
