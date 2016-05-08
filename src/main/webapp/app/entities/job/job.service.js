(function() {
    'use strict';
    angular
        .module('crowdwareApp')
        .factory('Job', Job);

    Job.$inject = ['$resource'];

    function Job ($resource) {
        var resourceUrl =  'api/jobs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
