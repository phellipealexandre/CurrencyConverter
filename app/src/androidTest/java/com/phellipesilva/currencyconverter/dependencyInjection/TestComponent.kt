package com.phellipesilva.currencyconverter.dependencyInjection

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceTestModule::class, DatabaseTestModule::class])
interface TestComponent : ApplicationComponent {

    @Component.Builder
    interface Builder : ApplicationComponent.Builder
}