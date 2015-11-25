(function() {
  'use strict';

  angular
    .module('documentmeUi')
    .run(runBlock);

  /** @ngInject */
  function runBlock($log) {

    $log.debug('runBlock end');
  }

})();
