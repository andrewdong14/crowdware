(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .factory('CrowdAppSearch', CrowdAppSearch);

    CrowdAppSearch.$inject = ['$resource'];

    function CrowdAppSearch($resource) {
        var resourceUrl =  'api/_search/crowd-apps/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
