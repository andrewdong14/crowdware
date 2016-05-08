(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app', {
            parent: 'entity',
            url: '/app?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Apps'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/app/apps.html',
                    controller: 'AppController',
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
        .state('app-detail', {
            parent: 'entity',
            url: '/app/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'App'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/app/app-detail.html',
                    controller: 'AppDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'App', function($stateParams, App) {
                    return App.get({id : $stateParams.id});
                }]
            }
        })
        .state('app.new', {
            parent: 'app',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/app/app-dialog.html',
                    controller: 'AppDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                source: null,
                                version: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('app', null, { reload: true });
                }, function() {
                    $state.go('app');
                });
            }]
        })
        .state('app.edit', {
            parent: 'app',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/app/app-dialog.html',
                    controller: 'AppDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['App', function(App) {
                            return App.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('app', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.delete', {
            parent: 'app',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/app/app-delete-dialog.html',
                    controller: 'AppDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['App', function(App) {
                            return App.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('app', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
