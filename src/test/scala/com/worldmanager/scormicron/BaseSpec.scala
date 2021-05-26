package com.worldmanager.scormicron

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen, TestSuite}

abstract class BaseSpec extends TestSuite
    with AnyFunSpecLike
    with Matchers
    with GivenWhenThen
    with BeforeAndAfterAll
    with BeforeAndAfterEach