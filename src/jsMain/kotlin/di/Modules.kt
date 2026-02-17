package io.github.svbgabriel.di

import io.github.svbgabriel.infrastructure.web.controller.ContactController
import io.github.svbgabriel.infrastructure.database.Database
import io.github.svbgabriel.infrastructure.persistence.ContactRepositoryImpl
import io.github.svbgabriel.domain.repository.ContactRepository
import io.github.svbgabriel.domain.service.ContactService
import io.github.svbgabriel.domain.application.service.ContactServiceImpl
import org.koin.dsl.module

val appModule = module {
    single { Database() }
    single<ContactRepository> { ContactRepositoryImpl() }
    single<ContactService> { ContactServiceImpl(get()) }
    single { ContactController(get()) }
}
