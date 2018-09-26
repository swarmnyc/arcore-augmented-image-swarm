package com.swarmnyc.arswarm.app

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.ar.sceneform.HitTestResult
import com.swarmnyc.arswarm.BuildConfig
import com.swarmnyc.arswarm.R
import com.swarmnyc.arswarm.ar.AugmentedImageNode
import com.swarmnyc.arswarm.utils.Logger

class MainActivity : ArBaseActivity() {
    private lateinit var arFragment: SwarmArFragment
    private var selectNode: AugmentedImageNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun startAr() {
        arFragment = SwarmArFragment()

        supportFragmentManager.beginTransaction().replace(R.id.ar_fragment, arFragment).commit()

        arFragment.setOnStarted = {
            arFragment.arSceneView.scene.setOnTouchListener(::handleTouch)

            debugInit()
        }
    }

    private fun handleTouch(hitTestResult: HitTestResult, event: MotionEvent): Boolean {
        Logger.d("touched: ${hitTestResult.node}")

        selectNode = hitTestResult.node as? AugmentedImageNode
        return if (selectNode != null) {
            Toast.makeText(this, "${selectNode?.name} selected", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun debugInit() {
        if (BuildConfig.DEBUG) {
            findViewById<View>(R.id.debug_panel).visibility = View.VISIBLE
            val offset = 0.001f
            findViewById<View>(R.id.add_x).setOnClickListener {
                selectNode?.modifyLayout {
                    offsetX += offset
                }
            }
            findViewById<View>(R.id.add_y).setOnClickListener {
                selectNode?.modifyLayout {
                    offsetY += offset
                }
            }
            findViewById<View>(R.id.add_z).setOnClickListener {
                selectNode?.modifyLayout {
                    offsetZ += offset
                }
            }
            findViewById<View>(R.id.minus_x).setOnClickListener {
                selectNode?.modifyLayout {
                    offsetX -= offset
                }
            }
            findViewById<View>(R.id.minus_y).setOnClickListener {
                selectNode?.modifyLayout {
                    offsetY -= offset
                }
            }
            findViewById<View>(R.id.minus_z).setOnClickListener {
                selectNode?.modifyLayout {
                    offsetZ -= offset
                }
            }

            findViewById<View>(R.id.scale_up).setOnClickListener {
                selectNode?.modifyLayout {
                    scaledWidth += offset
                    scaledHeight += offset
                    scaledDeep += offset
                }
            }

            findViewById<View>(R.id.scale_down).setOnClickListener {
                selectNode?.modifyLayout {
                    scaledWidth -= offset
                    scaledHeight -= offset
                    scaledDeep -= offset

                }
            }
        }
    }
}

