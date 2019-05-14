package com.worldmanager.scormicron

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSpec, GivenWhenThen, Matchers}

abstract class BaseSpec extends FunSpec
    with Matchers
    with GivenWhenThen
    with BeforeAndAfterAll
    with BeforeAndAfterEach