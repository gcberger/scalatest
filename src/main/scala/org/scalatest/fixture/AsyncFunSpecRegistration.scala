/*
 * Copyright 2001-2014 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest.fixture

import scala.concurrent.Future
import org.scalatest._

trait AsyncFunSpecRegistration extends FunSpecRegistration with AsyncFixtures { thisSuite =>

  import engine._

  protected override def runTest(testName: String, args: Args): Status = {

    def invokeWithAsyncFixture(theTest: TestLeaf): AsyncOutcome = {
      FutureOutcome(
        theTest.testFun match {
          case transformer: org.scalatest.fixture.Transformer[_] =>
            transformer.exceptionalTestFun match {
              case wrapper: NoArgAsyncTestWrapper[_] =>
                withAsyncFixture(new FixturelessAsyncTestFunAndConfigMap(testName, wrapper.test, args.configMap))
              case fun: (FixtureParam => Future[_]) => withAsyncFixture(new AsyncTestFunAndConfigMap(testName, fun, args.configMap))
              //case fun => withAsyncFixture(new AsyncTestFunAndConfigMap(testName, fun, args.configMap))
            }
          case other =>
            other match {
              case wrapper: NoArgAsyncTestWrapper[_] =>
                withAsyncFixture(new FixturelessAsyncTestFunAndConfigMap(testName, wrapper.test, args.configMap))
              case fun: (FixtureParam => Future[_]) => withAsyncFixture(new AsyncTestFunAndConfigMap(testName, fun, args.configMap))
              //case fun => withAsyncFixture(new AsyncTestFunAndConfigMap(testName, fun, args.configMap))
            }
        }
      )
    }

    runTestImpl(thisSuite, testName, args, true, invokeWithAsyncFixture)
  }

}