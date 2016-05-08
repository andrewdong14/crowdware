(function() {
    'use strict';
    angular
        .module('crowdwareApp')
        .factory('App', App);

    App.$inject = ['$resource'];

    function App ($resource) {
        var resourceUrl =  'api/apps/:id';

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
