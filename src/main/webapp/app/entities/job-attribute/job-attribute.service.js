(function() {
    'use strict';
    angular
        .module('crowdwareApp')
        .factory('JobAttribute', JobAttribute);

    JobAttribute.$inject = ['$resource'];

    function JobAttribute ($resource) {
        var resourceUrl =  'api/job-attributes/:id';

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
