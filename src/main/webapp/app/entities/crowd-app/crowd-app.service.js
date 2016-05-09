(function() {
    'use strict';
    angular
        .module('crowdwareApp')
        .factory('CrowdApp', CrowdApp);

    CrowdApp.$inject = ['$resource'];

    function CrowdApp ($resource) {
        var resourceUrl =  'api/crowd-apps/:id';

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
