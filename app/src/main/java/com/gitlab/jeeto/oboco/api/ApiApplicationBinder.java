package com.gitlab.jeeto.oboco.api;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

import com.gitlab.jeeto.oboco.api.v1.book.BookDtoMapper;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollectionDtoMapper;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookCollectionMarkDtoMapper;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkDtoMapper;
import com.gitlab.jeeto.oboco.api.v1.user.UserDtoMapper;
import com.gitlab.jeeto.oboco.data.bookscanner.BookScanner;
import com.gitlab.jeeto.oboco.data.bookscanner.DefaultBookScanner;
import com.gitlab.jeeto.oboco.database.EntityManagerFactory;
import com.gitlab.jeeto.oboco.database.EntityManagerFactoryFactory;
import com.gitlab.jeeto.oboco.database.book.BookService;
import com.gitlab.jeeto.oboco.database.bookcollection.BookCollectionService;
import com.gitlab.jeeto.oboco.database.bookmark.BookMarkService;
import com.gitlab.jeeto.oboco.database.user.UserService;
import com.gitlab.jeeto.oboco.server.authentication.UserTokenService;

public class ApiApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
    	bindFactory(EntityManagerFactoryFactory.class).proxy(true).proxyForSameScope(false).to(javax.persistence.EntityManagerFactory.class).in(Singleton.class);
    	bindFactory(EntityManagerFactory.class).proxy(true).proxyForSameScope(false).to(javax.persistence.EntityManager.class).in(RequestScoped.class);
    	bind(BookService.class).to(BookService.class);
    	bind(BookDtoMapper.class).to(BookDtoMapper.class);
    	bind(BookCollectionService.class).to(BookCollectionService.class);
    	bind(BookCollectionDtoMapper.class).to(BookCollectionDtoMapper.class);
    	bind(BookMarkService.class).to(BookMarkService.class);
    	bind(BookMarkDtoMapper.class).to(BookMarkDtoMapper.class);
    	bind(BookCollectionMarkDtoMapper.class).to(BookCollectionMarkDtoMapper.class);
    	bind(DefaultBookScanner.class).named("DEFAULT").to(BookScanner.class).in(Singleton.class);
        bind(UserService.class).to(UserService.class);
        bind(UserDtoMapper.class).to(UserDtoMapper.class);
        bind(UserTokenService.class).to(UserTokenService.class);
    }
}
