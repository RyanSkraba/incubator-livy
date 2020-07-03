/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.livy.repl

import org.apache.spark.SparkConf
import org.json4s.{DefaultFormats, JNull, JValue}
import org.json4s.JsonDSL._
import org.scalatest._

import org.apache.livy.rsc.driver.SparkEntries
import org.apache.livy.sessions._

abstract class PythonBaseInterpreterSpec extends BaseInterpreterSpec {

  it should "execute `1 + 2` == 3" in withInterpreter { interpreter =>
    val response = interpreter.execute("1 + 2")
    response should equal (Interpreter.ExecuteSuccess(
      TEXT_PLAIN -> "3"
    ))
  }

}

class Python2InterpreterSpec extends PythonBaseInterpreterSpec with BeforeAndAfterAll {

  implicit val formats = DefaultFormats

  override def beforeAll(): Unit = {
    super.beforeAll()
    sys.props.put("pyspark.python", "python2")
  }

  override def createInterpreter(): Interpreter = {
    val sparkConf = new SparkConf()
    PythonInterpreter(sparkConf, new SparkEntries(sparkConf))
  }

  // Scalastyle is treating unicode escape as non ascii characters. Turn off the check.
  // scalastyle:off non.ascii.character.disallowed
  it should "print unicode correctly" in withInterpreter { intp =>
    val code = "print(u\"\u263A\")";
    val x = intp.execute("print(u\"\u263A\")")
    val y = "\u263A"
    System.out.println(intp.execute("print(u\"\u263A\")"))
    intp.execute("print(u\"\u263A\")") should equal(Interpreter.ExecuteSuccess(
      TEXT_PLAIN -> "\u263A"
    ))
//    System.out.println(intp.execute("""print(u"\u263A")"""))
//    intp.execute("""print(u"\u263A")""") should equal(Interpreter.ExecuteSuccess(
//      TEXT_PLAIN -> "\u263A"
//    ))
    intp.execute("""print("\xE2\x98\xBA")""") should equal(Interpreter.ExecuteSuccess(
      TEXT_PLAIN -> "\u263A"
    ))
  }
  // scalastyle:on non.ascii.character.disallowed
}
