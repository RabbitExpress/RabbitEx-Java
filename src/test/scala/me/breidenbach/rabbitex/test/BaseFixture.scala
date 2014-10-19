package me.breidenbach.rabbitex.test

/**
 * Date: 10/16/14
 * Time: 10:38 AM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfter, FunSuite}
import org.scalatest.mock.MockitoSugar


/**
 * Date: 10/11/14
 * Time: 11:37 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
trait BaseFixture extends FunSuite with AnswerSugar with MockitoSugar with BeforeAndAfter with BeforeAndAfterEach {

}