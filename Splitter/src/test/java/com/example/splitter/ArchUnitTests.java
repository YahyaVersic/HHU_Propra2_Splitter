package com.example.splitter;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

@AnalyzeClasses(packagesOf = SplitterApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTests {

  @ArchTest
  ArchRule noMembersShouldBeAutowired = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

  @ArchTest
  ArchRule noClassesShouldThrowGenericExceptions = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

  @ArchTest
  ArchRule onlyPrivateMembersCanAccessController = ArchRuleDefinition.fields().that()
      .areDeclaredInClassesThat().areAnnotatedWith(
          Controller.class)
      .should().bePrivate();

  @ArchTest
  ArchRule onlyControllerClassesCanAccessOtherControllerClasses = noClasses()
      .that().areNotAnnotatedWith(Controller.class)
      .should().accessClassesThat().areAnnotatedWith(Controller.class);

  //Onion architecture test
  @ArchTest
  ArchRule onionTest = onionArchitecture()
      .domainModels("..domain.model..")
      .domainServices("..domain.services..")
      .applicationServices("..application.service..")
      .adapter("web", "..controller..")
      .adapter("repo", "..repositories..")
      .adapter("security", "..configuration..");

  @ArchTest
  ArchRule noControllerTest = noClasses().that().areAnnotatedWith(Controller.class).should()
      .accessClassesThat().areAnnotatedWith(
          Repository.class);


}
