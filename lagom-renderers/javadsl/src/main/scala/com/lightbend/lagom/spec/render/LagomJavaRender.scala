/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package com.lightbend.lagom.spec.render

import java.io.File
import java.util.regex.Matcher

import com.lightbend.lagom.spec.LagomGeneratorTypes
import com.lightbend.lagom.spec.LagomGeneratorTypes.{ GeneratedCode, Output }
import com.lightbend.lagom.spec.model.Service
import com.lightbend.lagom.spec.render.descriptor.JavaLagomDescriptorRender
import com.lightbend.lagom.spec.render.model.JavaModelRender

/**
 *
 */
object LagomJavaRender {

  val render: LagomGeneratorTypes.Render = { service =>
    val descriptorFileName = getPath(service, s"${service.interfaceName}")
    val descriptor: String = JavaLagomDescriptorRender.render(service)

    val customModels = service.customModels.map { model =>
      val content = JavaModelRender.render(service, model)
      val code = GeneratedCode(getPath(service, model.className), content)
      (model.className, code)
    }.toMap

    Output(GeneratedCode(descriptorFileName, descriptor), customModels)
  }

  /**
   * @return a relative path to the file.
   */
  private def getPath(service: Service, className: String): File = {
    val packageSplits = service.`package`.split("\\.")
    val directory: File = packageSplits.tail.foldLeft(new File(packageSplits.head))((acc, x) => new File(acc, x))
    new File(directory, s"${className}.java")
  }
}
