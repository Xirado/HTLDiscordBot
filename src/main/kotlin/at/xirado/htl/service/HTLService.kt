package at.xirado.htl.service

import at.xirado.htl.config.HTLConfig
import at.xirado.htl.config.HTLConfigProvider
import org.athena.service.ConfigurableService
import org.athena.service.ConfigurationProvider
import org.athena.service.services.main.db.DataService

interface HTLService : DataService, ConfigurableService<HTLConfigProvider, HTLConfig> {
    override val provider: ConfigurationProvider<HTLConfig>
        get() = HTLConfigProvider
}