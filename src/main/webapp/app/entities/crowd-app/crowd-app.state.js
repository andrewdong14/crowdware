(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('crowd-app', {
            parent: 'entity',
            url: '/crowd-app?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'CrowdApps'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/crowd-app/crowd-apps.html',
                    controller: 'CrowdAppController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('crowd-app-detail', {
            parent: 'entity',
            url: '/crowd-app/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'CrowdApp'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/crowd-app/crowd-app-detail.html',
                    controller: 'CrowdAppDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'CrowdApp', function($stateParams, CrowdApp) {
                    return CrowdApp.get({id : $stateParams.id});
                }]
            }
        })
        .state('crowd-app.new', {
            parent: 'crowd-app',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/crowd-app/crowd-app-dialog.html',
                    controller: 'CrowdAppDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                version: null,
                                source: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('crowd-app', null, { reload: true });
                }, function() {
                    $state.go('crowd-app');
                });
            }]
        })
        .state('crowd-app.edit', {
            parent: 'crowd-app',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/crowd-app/crowd-app-dialog.html',
                    controller: 'CrowdAppDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['CrowdApp', function(CrowdApp) {
                            return CrowdApp.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('crowd-app', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('crowd-app.delete', {
            parent: 'crowd-app',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/crowd-app/crowd-app-delete-dialog.html',
                    controller: 'CrowdAppDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['CrowdApp', function(CrowdApp) {
                            return CrowdApp.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('crowd-app', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
