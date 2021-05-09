Feature: Fund API

  Scenario: CRUD

    Given the test environment initialized

    When the following funds are created
      | Test Fund 1 | TF1 |
      | Test Fund 2 | TF2 |
      | Test Fund 3 | TF3 |

    Then all funds can be retrieved
    And a specific fund can be retrieved
    And a specific fund can be updated
    And a specific fund can be deleted