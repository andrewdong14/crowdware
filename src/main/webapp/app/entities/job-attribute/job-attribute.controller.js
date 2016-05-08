(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('JobAttributeController', JobAttributeController);

    JobAttributeController.$inject = ['$scope', '$state', 'JobAttribute', 'JobAttributeSearch'];

    function JobAttributeController ($scope, $state, JobAttribute, JobAttributeSearch) {
        var vm = this;
        vm.jobAttributes = [];
        vm.loadAll = function() {
            JobAttribute.query(function(result) {
                vm.jobAttributes = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            JobAttributeSearch.query({query: vm.searchQuery}, function(result) {
                vm.jobAttributes = result;
            });
        };
        vm.loadAll();
        
    }
})();
