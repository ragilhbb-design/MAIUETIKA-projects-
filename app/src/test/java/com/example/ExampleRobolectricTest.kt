package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MAIEUTIKA", appName)
  }

  @Test
  fun `chatViewModel can be instantiated by zero arg and application constructors`() {
    val vm1 = com.example.ui.ChatViewModel()
    val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm2 = com.example.ui.ChatViewModel(context)
    org.junit.Assert.assertNotNull(vm1.uiState.value)
    org.junit.Assert.assertNotNull(vm2.uiState.value)
    assertEquals(1, vm1.uiState.value.messages.size)
  }
}
