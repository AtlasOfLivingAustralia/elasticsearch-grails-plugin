package grails.plugins.elasticsearch

import grails.test.mixin.integration.Integration
import grails.core.GrailsApplication
import spock.lang.Specification
import test.*

@Integration
class AttachmentMappingIntegrationSpec extends Specification {

    ElasticSearchService elasticSearchService
    ElasticSearchAdminService elasticSearchAdminService
    ElasticSearchHelper elasticSearchHelper
    GrailsApplication grailsApplication

    void 'Index a File object'() {
        given:
        def contents = "It was the best of times, it was the worst of times"
        def file = new test.File(filename: 'myTestFile.txt', 
                                 attachment:contents.bytes.encodeBase64().toString())
        file.save(failOnError: true)

        when:
        elasticSearchAdminService.refresh() // Ensure the latest operations have been exposed on the ES instance

        and:
        elasticSearchService.search('best', [indices: File, types: File]).total == 1

        then:
        elasticSearchService.unindex(file)
        elasticSearchAdminService.refresh()

        and:
        elasticSearchService.search('best', [indices: File, types: File]).total == 0
    }
}
