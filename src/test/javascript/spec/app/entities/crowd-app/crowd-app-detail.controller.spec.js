'use strict';

describe('Controller Tests', function() {

    describe('CrowdApp Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCrowdApp, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCrowdApp = jasmine.createSpy('MockCrowdApp');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'CrowdApp': MockCrowdApp,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("CrowdAppDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'crowdwareApp:crowdAppUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
