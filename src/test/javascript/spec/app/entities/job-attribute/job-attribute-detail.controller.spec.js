'use strict';

describe('Controller Tests', function() {

    describe('JobAttribute Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockJobAttribute, MockJob;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockJobAttribute = jasmine.createSpy('MockJobAttribute');
            MockJob = jasmine.createSpy('MockJob');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'JobAttribute': MockJobAttribute,
                'Job': MockJob
            };
            createController = function() {
                $injector.get('$controller')("JobAttributeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'crowdwareApp:jobAttributeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
