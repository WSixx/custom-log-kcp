package br.com.brd.annotations

/**
 *
 * Annotation for Log when fun start and ends
 *
 * created on 27/02/2026
 * @author Lucas Goncalves
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DebugLog(
    val message: String = "",
    val level: String = "DEBUG",
)