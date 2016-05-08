(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .factory('AppSearch', AppSearch);

    AppSearch.$inject = ['$resource'];

    function AppSearch($resource) {
        var resourceUrl =  'api/_search/apps/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
