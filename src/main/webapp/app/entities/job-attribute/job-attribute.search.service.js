(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .factory('JobAttributeSearch', JobAttributeSearch);

    JobAttributeSearch.$inject = ['$resource'];

    function JobAttributeSearch($resource) {
        var resourceUrl =  'api/_search/job-attributes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
